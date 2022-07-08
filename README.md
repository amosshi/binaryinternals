## `freeinternals`: Binary Internals Viewer

An application to show binary file internals visually & interactively for the meaning of every byte.

Supported Formats

* [BMP](https://en.wikipedia.org/wiki/BMP_file_format) file
* [Class](https://docs.oracle.com/javase/specs/) file
  * Programming Logic: http://www.codeproject.com/Articles/35915/Java-Class-Viewer
  * Usage Sample: https://www.codeproject.com/Articles/762980/Inside-the-Hello-World-Application-via-Java-Class
* [JPEG](https://en.wikipedia.org/wiki/JPEG) file
* [PNG](https://en.wikipedia.org/wiki/Portable_Network_Graphics) file
* [ZIP](https://en.wikipedia.org/wiki/ZIP_(file_format)) file
  * [The structure of a PKZip file](https://users.cs.jmu.edu/buchhofp/forensics/formats/pkzip.html)
* Will supported formats
  * [DEX](https://en.wikipedia.org/wiki/Dalvik_(software)) file for Android (in progress)
  * [ELF](https://en.wikipedia.org/wiki/Executable_and_Linkable_Format) file for `*nix` systems (in progress)
 
Build the Source Code

* Java Version: `openjdk version "11.0.11" 2021-04-20`
  * You may choose to use `Java 8` to re-build if still in Java 8
* Build Tool: `Apache Maven 3.6.3`
  * Maven `3.5` or higher is needed because we are using the [Maven CI Friendly Versions](https://maven.apache.org/maven-ci-friendly.html) `${revision}`
* Edit `build.sh` file for the `JAVA_HOME` location, if needed
  * Curent script running on Ubuntu so using the folder `export JAVA_HOME=/usr/lib/jvm/default-java`
* Build scripts
  * `./build.sh` Do a quickly build, to get an running application
  * `./full-lifecycle-build.sh` Do a full build, via execute all targets like checkstyle, spotbugs, etc.
* Test case for Java/JVM `.class` format
  * `./masstest-format-class.sh` Parse all `.class` files in current Ubuntu linux system `default-java` folder
  * We can edit the `JAVA_FOLDER` variable if want to test with other Java versions

Download

* Download Application: https://github.com/amosshi/freeinternals/releases

User guide

* We need the `java` command to run this tool
  * `java -jar BinaryInternalsViewer-3.5.jar`
  * Menu item: `File` > `Open...`
  * Choose the binary file to view

Dependency

* This application do not rely on any 3rd party libraries, easy to add it to your project

Legacy note

* The orignal *Java Class Viewer* has been retired, and merged into *Binary Internals Viewer* since Apr 2021

