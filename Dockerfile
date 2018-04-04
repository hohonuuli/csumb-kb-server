FROM openjdk:8-jre

LABEL maintainer="Brian Schlining <brian@mbari.org>"

RUN mkdir -p /opt/kbserver

COPY target/csumb-kb-server-*-pack.zip /opt

WORKDIR /opt

RUN unzip *.zip -d /opt/kbserver && \
    rm *.zip && \
    chmod a+x /opt/kbserver/bin/main.sh

EXPOSE 4567

CMD ["/opt/kbserver/bin/main.sh"]
