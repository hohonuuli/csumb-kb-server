FROM maven:3-jdk-8

LABEL maintainer="Brian Schiling"

Env APP_HOME /opt/kbserver

COPY . ${APP_HOME}

WORKDIR ${APP_HOME}

EXPOSE 4567

ENTRYPOINT ["./run.sh","main"] 