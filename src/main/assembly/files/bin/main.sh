#!/usr/bin/env bash

APP_HOME=`dirname "$0"`/..
APP_CLASSPATH=$APP_HOME/conf:$APP_HOME/lib/*

${JAVACMD:=java} -Xms16m -Xmx512m \
         -Duser.timezone=UTC \
         -Dfile.encoding=UTF8 \
         -classpath "$APP_CLASSPATH" \
org.mbari.m3.kbserver.Main "$@"