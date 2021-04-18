# freeinternals

## Binary Internals Viewer

An application to show binary file visually & interactively for the meaning of every byte.

Download
* Download Application: https://github.com/amosshi/freeinternals/releases

Supported Formats
* Class file for JVM platform
  * Programming Logic: http://www.codeproject.com/Articles/35915/Java-Class-Viewer
  * Usage Sample: https://www.codeproject.com/Articles/762980/Inside-the-Hello-World-Application-via-Java-Class
* BMP file
* JPEG file
* PNG file
* ZIP file
* DEX file for Android (in progress)
* ELF file for `*nix` systems (todo)
* PE file for Windows (todo)

Legacy note
* The orignal *Java Class Viewer* has been merged into *Binary Internals Viewer* since Apr 2021
 
Build the Source Code

* We require the maven command line `mvn` has been installed
* Edit `build.sh` file for the `JAVA_HOME` location, if needed
* `./build.sh` Build the source code and create the deploy packages

