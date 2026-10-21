#!/bin/bash
#
# Mass test for Java Class Viewer
#   This script is trying to test on all the .class files in the JAVA_HOME/jmods/*.jmod files
#   This script is designed to execute on Ubuntu Linux
#
# Parameter
#   We can edit the JAVA_FOLDER variable in the script if want to test with other Java versions
#

# Java folder location, default to current JAVA_HOME
JAVA_FOLDER=$JAVA_HOME

# The viewer jar has a build timestamp in its name, e.g. BinaryInternalsViewer-3.7-20261019.061545.jar
# Use the newest one, skipping the -sources and -javadoc jars
VIEWER_JAR=$(ls -t BinaryInternalsViewer/target/BinaryInternalsViewer-*.jar 2>/dev/null \
  | grep -v -e '-sources\.jar$' -e '-javadoc\.jar$' \
  | head -1)
if [ -z "$VIEWER_JAR" ]; then
  echo "BinaryInternalsViewer jar not found in BinaryInternalsViewer/target/, please build it first: mvn install"
  exit 1
fi
# Absolute path, since the commands below run in target/masstest
VIEWER_JAR="$(cd "$(dirname "$VIEWER_JAR")" && pwd)/$(basename "$VIEWER_JAR")"
echo "Viewer jar: $VIEWER_JAR"


logtime() {
  retval=$(date '+%Y-%m-%d.%T.%3N')
  echo $retval
}

mkdir -p   target/masstest
cd         target/masstest && pwd
rm    -rf  *

touch      masstest.cmds
find "$JAVA_FOLDER/jmods/" -iname *.jmod | while read jmodfilename; do
  jmodfilename_nopath=$(basename -- "$jmodfilename")
  jmodfilename_short="${jmodfilename_nopath%.*}"

  echo "$(logtime) Processing $jmodfilename , filename=$jmodfilename_nopath shortfilename=$jmodfilename_short"

  # Get and Extract the file
  cp       $jmodfilename .
  unzip -q $jmodfilename_nopath -d $jmodfilename_short

  # Processing each .class file
  find $jmodfilename_short -iname *.class | while read classfilename; do
    echo "timeout 5 java -Dorg.binaryinternals.masstestmode=true -jar '$VIEWER_JAR' '$classfilename'" >> masstest.cmds
  done

  # Clean up .jmod file
  rm       $jmodfilename_nopath
done

echo "$(logtime) Commands sample"
tail -10 masstest.cmds

echo "$(logtime) Mass Test Starts"

parallel --ungroup -j 50% < masstest.cmds

# Go back
cd ../../
echo "$(logtime) Finished"
