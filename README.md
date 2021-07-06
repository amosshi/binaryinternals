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

Will supported formats
* [DEX](https://en.wikipedia.org/wiki/Dalvik_(software)) file for Android (in progress)
* [ELF](https://en.wikipedia.org/wiki/Executable_and_Linkable_Format) file for `*nix` systems (todo)
* [PE](https://en.wikipedia.org/wiki/Portable_Executable) file for Windows (todo)

Legacy note
* The orignal *Java Class Viewer* has been retired, and merged into *Binary Internals Viewer* since Apr 2021
 
Build the Source Code

* We require the maven command line `mvn` has been installed
* Edit `build.sh` file for the `JAVA_HOME` location, if needed
* Run Either build scripts
  * `./build.sh` Do a quickly build, to get an running application
  * `./build-full-lifecycle.sh` Do a full build, via execute all targets like checkstyle, spotbugs, etc.

Test Case
* Test case for Java/JVM `.class` format
  * `./masstest-format-class.sh` Parse all `.class` files in current Ubuntu linux system `default-java` folder

Download
* Download Application: https://github.com/amosshi/freeinternals/releases

Usage guide

* We need the `java` command to run this tool
  * `java -jar BinaryInternalsViewer-3.0.jar`
  * Menu item: `File` > `Open...`
  * Choose the binary file to view
