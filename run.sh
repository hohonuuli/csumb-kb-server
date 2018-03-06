#!/bin/bash

if [ $# -ne 1 ]; then
    echo "ERROR: Didn't' provide file to execute."
    exit 1
fi

if [ $1 = "main" ]; then
	mvn compile && mvn exec:java -Dexec.mainClass=org.mbari.m3.kbserver.Main

else
	mvn compile && mvn exec:java -Dexec.mainClass=org.mbari.m3.kbserver.examples.$1
fi
