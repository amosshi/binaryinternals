#!/bin/bash
#

# We will use the system default java on Ubunut Linux, could be changed if needed
export JAVA_HOME=/usr/lib/jvm/default-java

mvn clean && mvn package && mvn install
mvn javadoc:aggregate
