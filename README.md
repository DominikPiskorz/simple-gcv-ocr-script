# Simple GCV OCR Script
Basic Scala script for sending image requests to Google's CloudVision OCR REST API.  
Creates an output file with the response from GCV containing all of the document texts with locations.  

Sends images either from local files or urls.  
Currently support only jpgs and pngs, if you want to send your pdf using this script you'll need to convert it to multiple jpgs.


## What you need
- [Ammonite](https://ammonite.io/#Ammonite-REPL) for launching Scala scripts
- [GCV project and access token](https://cloud.google.com/vision/docs/setup)

## Usage
Generate your access token and put it in the `.gcv_token` file in the same directory as script:
```bash
gcloud auth application-default print-access-token > .gcv_token
```
Launch the script using ammonite, provide either url or local path to each image file you want to send as a separate argument separated with space:
```bash
amm simpleGcvOcr.sc url1 url2 local_file1 local_file2
```
Order of the files doesn't matter. The response from GCV will be saved in an `out.json` file in the same directory.

You may want to change default paths to token and output files, if you do simply change the following lines inside `simpleGcvOcr.sc`:
```scala
val tokenFile = ".gcv_token"
val outFile   = "out.json"
```

##  Text documents vs pictures
This script in default is configured for text-dense documents, though you can use it for pictures containing sparse text (see "Document text detection" vs "Text detection" https://cloud.google.com/vision/docs/features-list).

If you want to send requests for text-sparse pictures, simply change the line
```scala
val featureType = "DOCUMENT_TEXT_DETECTION"
```
to
```scala
val featureType = "TEXT_DETECTION"
```

## Dependancies
- [softwaremill/sttp](https://github.com/softwaremill/sttp) for creating and sending requests
- [circe](https://circe.github.io/circe/) for JSON serialization.
