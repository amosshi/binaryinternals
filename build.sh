#!/bin/bash
#

# Please change the JAVA_HOME according to the current system settings

# System default java on Ubuntu LTS Linux
export JAVA_HOME=/usr/lib/jvm/default-java

# System default java on openSUSE Linux
#export JAVA_HOME=/usr/lib64/jvm/java

mvn clean package install

mvn javadoc:javadoc
mvn javadoc:jar
mvn javadoc:aggregate
mvn javadoc:aggregate-jar

mvn source:jar
mvn source:aggregate

mvn checkstyle:checkstyle
mvn checkstyle:checkstyle-aggregate
mvn checkstyle:check
mvn pmd:pmd

mvn pdf:pdf

mvn jdeps:jdkinternals
mvn versions:display-dependency-updates

# mvn deploy:deploy
# mvn spotbugs:gui
