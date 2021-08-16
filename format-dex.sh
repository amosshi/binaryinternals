#!/bin/bash
#
# Build one format only
#

export JAVA_HOME=/usr/lib/jvm/default-java
mvn  --projects FormatDEX,BinaryInternalsViewer  clean package install

