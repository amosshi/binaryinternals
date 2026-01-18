#!/bin/bash
#
# Build one format only
#


export JAVA_HOME=/usr/lib/jvm/default-java
java -version

mvn  --projects CommonLib,FormatCLASS,BinaryInternalsViewer  clean package install
