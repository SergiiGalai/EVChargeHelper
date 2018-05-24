@echo off
for /f "tokens=1,2 delims==" %%a in (..\batchconfig.ini) do (
if %%a==CloudStorage set RootDir=%%b
)
SET SrcPath=.\app\build\outputs\apk\debug
SET TargetDir=%RootDir%\EVChargeHelper\output

ECHO %TargetDir%
XCOPY /Y %SrcPath%\*.apk %TargetDir%\chargeHelper.*