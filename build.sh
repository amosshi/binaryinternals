#!/bin/bash
#
# Build quickly
#

# Please change the JAVA_HOME according to the current system settings

# Set java home on Ubuntu LTS Linux
export JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64

# Set java home on openSUSE Linux
#export JAVA_HOME=/usr/lib64/jvm/java

# Set java home on MacOS
#JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-25.1.jdk/Contents/Home

# Set java home on Windows
# - Reference: https://www.theserverside.com/feature/How-to-set-JAVA_HOME-in-Windows-and-echo-the-result
# - Example value: JAVA_HOME=C:\data\tools\jdk

java -version

mvn  clean
mvn  package
mvn  install
mvn  dependency:tree
mvn  versions:display-dependency-updates
#mvn source:aggregate javadoc:aggregate javadoc:aggregate-jar pdf:aggregate checkstyle:checkstyle-aggregate pmd:aggregate-pmd site:site site:deploy 
#mvn jdeps:jdkinternals
#mvn spotbugs:gui
