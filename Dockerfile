FROM zenika/alpine-chrome

USER root

RUN apk --update upgrade && apk add --no-cache --update-cache --upgrade --latest openjdk17-jre-headless

ADD build/distributions/chromium-pdf-service.tar ./
WORKDIR ./chromium-pdf-service

USER chrome

EXPOSE 8080
EXPOSE 6001

ENV CHROME_PATH=/usr/bin/chromium-browser

ENTRYPOINT ["bin/chromium-pdf-service"]
