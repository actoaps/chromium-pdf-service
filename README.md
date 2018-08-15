Acto chromium-pdf-service
===
Acto chromium-pdf-service is a small microservice built with Spring Boot and Jersey. It uses Headless Chromium and the
Chrome Development Tools to load a website, and print it as PDF. It supports both GET and POST methods, and is designed
for deployment using docker.

Configuration is supplied through en environment variable called
```ACTO_CONF``` in JSON format. It let's you set 3 parameters:

    {
        "enabledGet": true,
        "enabledPost": true,
        "secret": "secret"
    }

Each element means:

* enableGet - if set to true, enables the GET endpoint.

* enablePost - if set to true, enables the POST endpoint.

* secret - the Authorization token you must send to use the service.

If you do not supply an ACTO_CONF variable, the above settings will be used.

Using The Service
---
The service by default runs on port 8888. The simplest way to change this (other than in the code), is remapping it in
Docker.  Additionally a Java debugger is exposed on port 5888, which you probably don't want to expose to the world.

The service exposes two endpoints, one is a GET, one is a PORT. Both return a PDF file, as an "attachment", meaning
that the browser/client will automatically download it. 

GET
---
The GET endpoint is ```/{url}/{filename}``` where ```{url}``` is the *Url Encoded* url of the page you want to print,
and ```{filename}``` is the name of the file you want back.

POST
---
The POST endpoint is ```/``` and requires the following JSON in the body

    {
        "url": "https;//www.acto.dk/",
        "file": "acto.pdf"
    }

Otherwise it is identical to the GET version.

You must send and Authorization Header with the "secret" Bearer token you set in ```ACTO_CONF``` or you will get a 401.