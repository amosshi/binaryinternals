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
mvn --version

rm -rf dist
mvn dependency:purge-local-repository -DreResolve=true

mvn  clean install javadoc:aggregate-no-fork
mvn  dependency:tree versions:display-dependency-updates
#mvn source:aggregate javadoc:aggregate javadoc:aggregate-jar pdf:aggregate checkstyle:checkstyle-aggregate pmd:aggregate-pmd site:site site:deploy 
#mvn spotbugs:gui
