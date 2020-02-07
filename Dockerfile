FROM openjdk:13-jdk-oraclelinux7
COPY chrome-chrome.repo /etc/yum.repos.d/google-chrome.repo
RUN yum install -y google-chrome-stable
ADD build/distributions/chromium-pdf-service.tar ./
WORKDIR ./chromium-pdf-service
EXPOSE 8888
EXPOSE 5888

ENTRYPOINT ["bin/chromium-pdf-service"]
