@REM Build quickly on Windows
@REM
@REM We assume JAVA_HOME has been set
@REM

echo %JAVA_HOME%
java -version

mvn  clean package install source:aggregate javadoc:aggregate javadoc:aggregate-jar pdf:aggregate checkstyle:checkstyle-aggregate pmd:aggregate-pmd site:site site:deploy versions:display-dependency-updates
