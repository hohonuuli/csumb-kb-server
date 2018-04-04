@echo off
REM $Id: $
title VARS/M3 - Knowledgebase Server
SET APP_HOME=%~dp0..
SET APP_CLASSPATH="%VARS_HOME%\conf";"%APP_HOME%\lib\*"

echo [VARS/M3] Starting VARS Knowledgebase Server Application
java -cp %APP_CLASSPATH% -Xms64m -Xmx256m -Duser.timezone=UTC org.mbari.m3.kbserver.Main