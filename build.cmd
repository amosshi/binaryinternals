@REM Build quickly on Windows

@REM We assume JAVA_HOME has been set
echo %JAVA_HOME%
java -version

mvn  clean package install javadoc:aggregate jdeps:jdkinternals versions:display-dependency-updates

echo  "Finished"
