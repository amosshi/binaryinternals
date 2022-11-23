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

java -version

mvn  clean package install
mvn  javadoc:aggregate
mvn  jdeps:jdkinternals
mvn  versions:display-dependency-updates

# Package
#   Add git revision info to version.log
#   Save to dist folder

mkdir -p dist
cd  BinaryInternalsViewer/target && pwd
git log -1 --format='%H %aI' > version.log
zip -r "../../dist/BinaryInternalsViewer-3.5_$(date '+%Y-%m-%d_%H.%M.%S').zip" BinaryInternalsViewer-3.5.jar libs/ version.log

echo  "$0 Finished"

