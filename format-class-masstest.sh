#!/bin/bash
#
# Mass test for Java Class Viewer
#   This script is trying to test on all the .class files in the JAVA_HOME/jmods/*.jmod files
#   This script is designed to execute on Ubuntu Linux
#
# Parameter
#   We can edit the JAVA_FOLDER variable in the script if want to test with other Java versions
#

# Java folder location, in Ubunut linux
JAVA_FOLDER="/usr/lib/jvm/default-java"


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
    echo "timeout 5 java -Dorg.binaryinternals.masstestmode=true -jar ../../BinaryInternalsViewer/target/BinaryInternalsViewer-3.6.jar '$classfilename'" >> masstest.cmds
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
