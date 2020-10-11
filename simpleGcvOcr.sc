import $ivy.{
  `com.softwaremill.sttp.client::core:2.2.8`,
  `com.softwaremill.sttp.client::circe:2.2.8`
}
import $file.utils.gcvObjects

import java.net.URL
import java.nio.file.{Paths, Files}
import java.util.Base64

import reflect.io.File
import scala.io.Source
import scala.util.Try

import sttp.client.quick._
import sttp.client.circe._
import io.circe.generic.auto._
import io.circe.syntax._

import gcvObjects._

@main
def main(paths: String*): Unit =
  if (paths.isEmpty || paths.contains("-h") || paths.contains("--help"))
    println(helpMsg)
  else if (!Files.exists(Paths.get(tokenFile)))
    println(tokenMsg)
  else {
    val token = 
      Source.fromFile(tokenFile)
        .getLines
        .next

    val parsedPaths = paths.toSeq.map { p =>
      Try(new URL(p)).toOption -> Paths.get(p)
    }

    val urls = parsedPaths.collect {
      case (Some(url), _) => url
    }

    val filePaths = parsedPaths.collect {
      case (None, fPath) if Files.exists(fPath) => fPath
    }

    val urlRequests = urls.map { u =>
      val imageSource = ImageSource(u.toString)
      val image = ImageFromSource(imageSource)

      OcrRequest(image, Seq(feature))
    }

    val fileRequests = filePaths.map { f =>
      val bytes = Files.readAllBytes(f)
      val content = Base64.getEncoder.encodeToString(bytes)
      val image = ImageFromContent(content)


      OcrRequest(image, Seq(feature))
    }

    val allRequests = urlRequests ++ fileRequests

    val body = Map("requests" -> allRequests)

    val request = 
      basicRequest
        .body(body.asJson)
        .auth.bearer(token)
        .contentType("application/json", "utf-8")
        .post(gcvUri)

    println(s"Sending ${urlRequests.size} urls " +
      s"and ${fileRequests.size} local files to GCV...")

    //val response = request.send()

    //File(outFile).writeAll(response.toString)

    println("Saved response from GCV for " +
      s"${allRequests.size} requests to file '$outFile'.")
  }

val gcvUri = uri"https://vision.googleapis.com/v1/images:annotate"

// OCR request meant for text-dense images (documents).
// If you want to OCR pictures with sparse text, change this to "TEXT_DETECTION".
val featureType = "DOCUMENT_TEXT_DETECTION"

// Change these values if you want different paths to token or result file: 
val tokenFile = ".gcv_token"
val outFile = "out"

val helpMsg = 
  "simpleGcvOcr - Sends document text detection requests " +
  "to Google's CloudVision API.\n\n" +
  "Usage: Simply provide either url or local path to each file " +
  "you want to send as a separate argument separated with space, " +
  "order doesn't matter. Like this:\n" +
  "amm simpleGcvOcr.sc url1 url2 local_file1 local_file2"
val tokenMsg =
  s"You must first create '$tokenFile' file " +
  "containing your CloudVision token:\n" +
  s"gcloud auth application-default print-access-token > $tokenFile"

val feature = Feature(
  featureType,
  1,
  "builtin/latest"
)
