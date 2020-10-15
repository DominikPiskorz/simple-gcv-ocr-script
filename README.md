# Simple GCV OCR Script
Basic Scala script for sending image requests to Google's CloudVision API for OCR.
Sends images either from local files or urls.
Creates an output file with the response from GCV containing all of the document texts with locations.
Currently support only jpgs and pngs, you'll need to convert your pdf to multiple jpgs if you want to send it.


## What you need
- Ammonite for launching scala scripts - https://ammonite.io/#Ammonite-REPL
- GCV project and access token - https://cloud.google.com/vision/docs/setup

## Usage
Generate your access token and put it in the `.gcv_token` file in the same directory as script:
```
gcloud auth application-default print-access-token > .gcv_token
```
Launch the script using ammonite, provide either url or local path to each image file you want to send as a separate argument separated with space:
```
amm simpleGcvOcr.sc url1 url2 local_file1 local_file2
```
Order of the files doesn't matter. The response from GCV will be saved in an `out.json` file in the same directory.

You may want to change default paths to token and output files, if you do simply change the following lines inside `simpleGcvOcr.sc`:
```
val tokenFile = ".gcv_token"
val outFile   = "out.json"
```
