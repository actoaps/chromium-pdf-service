FROM zenika/alpine-chrome

USER root

RUN apk add --no-cache openjdk17-jre-headless

ADD build/distributions/chromium-pdf-service.tar ./
WORKDIR ./chromium-pdf-service

EXPOSE 8080
EXPOSE 6001

ENV CHROME_PATH=/usr/bin/chromium-browser

ENTRYPOINT ["bin/chromium-pdf-service"]
