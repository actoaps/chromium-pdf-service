FROM openjdk:13-jdk-oraclelinux7
RUN yum install -y wget unzip
RUN yum-config-manager --enable ol7_optional_latest
RUN yum install -y chromium
ADD build/distributions/chromium-pdf-service.tar ./
WORKDIR ./chromium-pdf-service
EXPOSE 8888
EXPOSE 5888

ENTRYPOINT ["bin/chromium-pdf-service"]
