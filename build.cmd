@REM Build quickly on Windows
@REM
@REM We assume JAVA_HOME has been set
@REM

echo %JAVA_HOME%
java -version

mvn  clean package install dependency:tree versions:display-dependency-updates
