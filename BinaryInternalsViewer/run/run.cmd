cd ..\dist
mkdir plugin
copy ..\..\FormatBMP\dist\FormatBMP.jar   .\plugin
copy ..\..\FormatDEX\dist\FormatDEX.jar   .\plugin
copy ..\..\FormatELF\dist\FormatELF.jar   .\plugin
copy ..\..\FormatJPEG\dist\FormatJPEG.jar .\plugin
copy ..\..\FormatPNG\dist\FormatPNG.jar   .\plugin
copy ..\..\FormatZIP\dist\FormatZIP.jar   .\plugin
copy ..\..\FormatPDF\dist\FormatPDF.jar   .\plugin
copy ..\..\FormatPE\dist\FormatPE.jar     .\plugin
copy ..\..\CommonLib\dist\CommonLib.jar   .\lib
java -jar BinaryInternalsViewer.jar

