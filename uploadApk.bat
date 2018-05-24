@echo off
SET SrcPath=.\app\build\outputs\apk\debug

for /f "tokens=1,2 delims==" %%a in (batchconfig.ini) do (
if %%a==CloudStorage set TargetDir=%%b
)

XCOPY /Y %SrcPath%\*.apk %TargetDir%\output\chargeHelper.*