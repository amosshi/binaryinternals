#!/bin/bash
#
# Build quickly
#

# Please change the JAVA_HOME according to the current system settings

# Set java home on Ubuntu LTS Linux
#export JAVA_HOME=/usr/lib/jvm/default-java
#export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

# Set java home on openSUSE Linux
#export JAVA_HOME=/usr/lib64/jvm/java

# Set java home on MacOS
#JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.0.3.1.jdk/Contents/Home

# Set java home on Windows
# - Reference: https://www.theserverside.com/feature/How-to-set-JAVA_HOME-in-Windows-and-echo-the-result
# - Example value: JAVA_HOME=C:\data\tools\jdk

java -version

git log -1 --format='%H %aI' > BinaryInternalsViewer/VERSION.log

mvn  clean package install
mvn  javadoc:aggregate
mvn  jdeps:jdkinternals
mvn  versions:display-dependency-updates

echo  "$0 Finished"
LTS
