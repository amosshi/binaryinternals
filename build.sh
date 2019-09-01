#!/bin/bash
#

# Please change the JAVA_HOME according to the current system settings

# System default java on Ubuntu LTS Linux
export JAVA_HOME=/usr/lib/jvm/default-java

# System default java on openSUSE Linux
#export JAVA_HOME=/usr/lib64/jvm/java

mvn clean package install
mvn javadoc:aggregate
