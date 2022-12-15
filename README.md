# Acto chromium-pdf-service
Acto chromium-pdf-service is a small microservice built with Spring Boot. It uses Headless Chromium and the
Chrome Development Tools to load a website, and print it as PDF. It supports both GET and POST methods, and is designed
for deployment using Docker.

For documentation on v1 see the [v1 README file](https://github.com/actoaps/chromium-pdf-service/blob/master/README-v1.md).

## Configuration
Configuration is supplied through environment variables. It let's you configure three parameters:

* ENABLED_GET - Whether the GET endpoint is enabled (default `true`).
* ENABLED_POST - Whether the POST endpoint is enabled (default `true`).
* SECRET - The Authorization bearer token you must send to the service (default `secret`).
* PDF_BUFFER_SIZE_MB - The Chrome WS buffer size. Too small of a buffer will cause requests surpassing the limit to
hang indefinitely. Read more [here](https://github.com/kklisura/chrome-devtools-java-client#api-hangs-ie-when-printing-pdfs)
(optional).
* CRHOME_PATH - The path to the Chrome/Chromium binary. If unset, it will try to detect it automatically (optional).

## Using The Service
The service by default runs on port 8080. The simplest way to change this (other than in the code), is remapping it in
Docker.  Additionally a Java debugger is exposed on port 6001, which you probably don't want to expose to the world.

The service exposes two endpoints, one is a GET, one is a POST. Both return a PDF file, as an "attachment", meaning
that the browser/client will automatically download it. 

Both endpoint expect an `Authorization` header with the configured secret. (By default: `Authorization: Bearer secret`)

### GET Endpoint
The GET endpoint is `/{url}/{filename}` where `{url}` is the *Url Encoded* url of the page you want to print,
and `{filename}` is the name of the file you want back.

### POST Endpoint
The POST endpoint is `/` and requires the following JSON in the body
```JSON
{
    "url": "https://www.acto.dk/",
    "file": "acto.pdf"
}
```
Otherwise it is identical to the GET version. 
