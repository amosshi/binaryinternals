## `freeinternals` Binary Internals Viewer

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
 
Download

* Download Application: https://github.com/amosshi/freeinternals/releases

User guide

* We need the `java` command to run this tool
  * `java -jar BinaryInternalsViewer-3.5.jar`
  * Menu item: `File` > `Open...`
  * Choose the binary file to view

Dependency

* This application do not rely on any 3rd party libraries other than JDK, easy to add it to your project

Build the Source Code

* Prerequisite
  * Java Version: `OpenJDK version 11` or higher
  * Build Tool: `Apache Maven 3.5` or higher: because we are using the [Maven CI Friendly Versions](https://maven.apache.org/maven-ci-friendly.html) `${revision}` feature
  * Set `JAVA_HOME` environment variable
* Build scripts
  * Linux/MacOS: `./build.sh` Do a quickly build, or `./full-lifecycle-build.sh` do a full build via execute all targets like checkstyle, spotbugs, etc.
    * Curent script running on Ubuntu so using the folder `export JAVA_HOME=/usr/lib/jvm/default-java`
    * Try to change the `JAVA_HOME` variable for different systems
  * Windows: `build.cmd`
* Test Case for Java `.class` format
  * `./masstest-format-class.sh` Parse all `.class` files in Ubuntu linux system `default-java` folder
  * We can edit the `JAVA_FOLDER` variable in the script if want to test with other Java versions

Legacy note

* The original `Java Class Viewer` has been retired and merged into `Binary Internals Viewer` since Apr 2021
