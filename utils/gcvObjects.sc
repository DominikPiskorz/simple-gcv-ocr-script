
import $ivy.`io.circe::circe-generic:0.12.3`

import io.circe.Encoder
import io.circe.syntax._
import io.circe.generic.auto._

final case class ImageSource(
  imageUri: String
)

sealed trait Image

final case class ImageFromSource(
  source: ImageSource
) extends Image

final case class ImageFromContent(
  content: String
) extends Image

final case class Feature(
  visionType: String,
  maxResults: Int,
  model:      String
)

object Feature { 
  implicit val encoder: Encoder[Feature] =
    Encoder.forProduct3("type", "maxResults", "model")(f => (f.visionType, f.maxResults, f.model))
}

final case class OcrRequest(
  image:    Image,
  features: Seq[Feature]
)

object OcrRequest { 
  implicit val encoder: Encoder[OcrRequest] =
    Encoder.forProduct2("image", "features") { 
        case OcrRequest(i: ImageFromSource, f)  => (i.asJson, f)
        case OcrRequest(i: ImageFromContent, f) => (i.asJson, f)
    }
}
