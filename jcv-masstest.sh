#!/bin/bash
#
# Mass test for Java Class Viewer
#   This script is trying to test on all the .class files in the JAVA_HOME/jmods/*.jmod files
#   This script is designed to execute on Ubuntu Linux
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
    echo "timeout 5 java -Djavaclassviewer.masstestmode=true -jar ../../JavaClassViewer/target/JavaClassViewer-12.0.jar '$classfilename'" >> masstest.cmds
#   timeout 5 java -Djavaclassviewer.masstestmode=true -jar ../../JavaClassViewer/target/JavaClassViewer-12.0.jar $classfilename
  done

  # Clean up .jmod file
  rm       $jmodfilename_nopath
done

echo "$(logtime) Commands"
tail -10 masstest.cmds

echo "$(logtime) Mass Test Starts"

# chmod +x masstest.cmds
# ./masstest.cmds

parallel --ungroup -j 50% < masstest.cmds

# Go back
cd ../../
echo "Finished"
