FROM openjdk:13-jdk-oraclelinux7
COPY google-chrome.repo /etc/yum.repos.d/google-chrome.repo
RUN yum install -y google-chrome-stable unzip
ADD https://noto-website-2.storage.googleapis.com/pkgs/NotoColorEmoji-unhinted.zip /usr/share/fonts/opentype/noto
COPY 01-emoji.conf /etc/fonts/conf.d/
RUN fc-cache -f -v
ADD build/distributions/chromium-pdf-service.tar ./
WORKDIR ./chromium-pdf-service
EXPOSE 8888
EXPOSE 5888

ENTRYPOINT ["bin/chromium-pdf-service"]
