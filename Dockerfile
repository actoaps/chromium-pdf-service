FROM amazoncorretto:17-alpine
RUN apk add chromium unzip findutils
COPY NotoColorEmoji.ttf /usr/share/fonts/opentype/noto/NotoColorEmoji.ttf
RUN fc-cache -f -v
ADD build/distributions/chromium-pdf-service.tar ./
WORKDIR ./chromium-pdf-service
EXPOSE 8080
EXPOSE 6001

ENTRYPOINT ["bin/chromium-pdf-service"]
