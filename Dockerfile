FROM openjdk:9.0.1-11-slim
RUN apt-get update && apt-get install -y chromium
ADD build/distributions/chromium-pdf-service.tar ./
WORKDIR ./chromium-pdf-service
EXPOSE 8888
EXPOSE 5888

ENTRYPOINT ["bin/chromium-pdf-service"]