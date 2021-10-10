#!/bin/bash
#
# Build with all targets
#

./build.sh

export JAVA_HOME=/usr/lib/jvm/default-java

mvn  source:jar
mvn  source:aggregate

mvn  javadoc:javadoc
mvn  javadoc:jar
mvn  javadoc:aggregate
mvn  javadoc:aggregate-jar

mvn  pdf:pdf

mvn  checkstyle:checkstyle
mvn  checkstyle:checkstyle-aggregate
mvn  checkstyle:check

mvn  pmd:pmd
mvn  spotbugs:spotbugs
#mvn spotbugs:gui

mvn  site
mvn  site:deploy
mvn  site:jar

#mvn deploy:deploy

