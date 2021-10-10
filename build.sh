#!/bin/bash
#
# Build quickly
#

# Please change the JAVA_HOME according to the current system settings

# System default java on Ubuntu LTS Linux
export JAVA_HOME=/usr/lib/jvm/default-java

# System default java on openSUSE Linux
#export JAVA_HOME=/usr/lib64/jvm/java

mvn  clean package install
mvn  javadoc:aggregate
mvn  jdeps:jdkinternals
mvn  versions:display-dependency-updates

# Package
cd BinaryInternalsViewer/target && pwd
# - Get current git revision
git log -1 --format='%H %aI' > version.log
# - Create Package
zip   -r "BinaryInternalsViewer-3.5_$(date '+%Y-%m-%d_%H.%M.%S').zip" BinaryInternalsViewer-3.5.jar libs/ version.log

echo  "$0 Finished"

