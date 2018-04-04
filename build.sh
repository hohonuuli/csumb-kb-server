#!/usr/bin/env bash

echo "--- Building kbserver"
mvn clean package && \
    docker build -t csumb/kbserver .
    # && docker build -t csumb/kbserver .
    # && docker push mbari/kbserver