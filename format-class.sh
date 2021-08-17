#!/bin/bash
#
# Build one format only
#

export JAVA_HOME=/usr/lib/jvm/default-java
mvn  --projects CommonLib,FormatCLASS,BinaryInternalsViewer  clean package install

