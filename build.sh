#!/bin/bash
#
# Build quickly
#

# Set JAVA_HOME explicitly to override automatic detection.

# Set java home on Ubuntu LTS Linux
#export JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64

# Set java home on openSUSE Linux
#export JAVA_HOME=/usr/lib64/jvm/java

# Set java home on MacOS
#JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-25.jdk/Contents/Home

# Set java home on Windows
# - Reference: https://www.theserverside.com/feature/How-to-set-JAVA_HOME-in-Windows-and-echo-the-result
# - Example value: JAVA_HOME=C:\data\tools\jdk


if [ -z "$JAVA_HOME" ] || [ ! -x "$JAVA_HOME/bin/java" ]; then
    echo "Error: JAVA_HOME is not set to a valid Java installation." >&2
    exit 1
fi

java -version
./mvnw --version

rm -rf dist

# Clean local Maven repository to ensure fresh dependencies
# To avoid silent-stale-jar trap
rm -rf ~/.m2/repository/org/binaryinternals

./mvnw  clean install
#./mvnw --projects CommonLib,FormatCLASS,BinaryInternalsViewer  clean package install
#./mvnw --projects CommonLib,FormatDEX,BinaryInternalsViewer    clean package install
#./mvnw --projects CommonLib,FormatZIP,BinaryInternalsViewer    clean package install
#./mvnw --projects FormatELF,BinaryInternalsViewer              clean package install
#./mvnw --projects FormatPDF,BinaryInternalsViewer              clean package install

# Dependency graph, shown in the README
./mvnw  depgraph:aggregate

./mvnw  javadoc:aggregate-no-fork
./mvnw  dependency:tree versions:display-dependency-updates versions:display-plugin-updates
#./mvnw source:aggregate javadoc:aggregate javadoc:aggregate-jar checkstyle:checkstyle-aggregate pmd:aggregate-pmd site:site site:deploy 
#./mvnw spotbugs:gui
