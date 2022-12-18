#!/bin/bash
#
# Build one format only
#


export JAVA_HOME=/usr/lib/jvm/default-java
#export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
java -version

mvn  --projects CommonLib,FormatCLASS,BinaryInternalsViewer  clean package install
