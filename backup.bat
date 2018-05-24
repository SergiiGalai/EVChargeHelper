@echo off
SET SrcPath=.\

for /f "tokens=1,2 delims==" %%a in (batchconfig.ini) do (
if %%a==CloudStorage set TargetDir=%%b
)

:: system date is in UA format
Set datestr=%DATE:~6,4%%DATE:~3,2%%DATE:~0,2%-%TIME:~0,2%%TIME:~3,2%
:: system date is in US format
:: Set datestr=%DATE:~10,4%%DATE:~7,2%%DATE:~4,2%-%TIME:~0,2%%TIME:~3,2%

::echo %datestr%
::echo %DATE%
::GOTO :Exit

::wait for user input
set /p comments= Enter check in comments for project: 

:BackupFiles
SET TargetFile=%TargetDir%\%datestr% %comments%.7z
ECHO -------------------------------------------
ECHO Create new project's backup file %TargetFile%
ECHO -------------------------------------------
call "c:\Program Files\7-Zip\7z" a -aoa -mx=5 -ms=on -r -ssw -t7z "%TargetFile%" "%SrcPath%\*" -xr!*.log -xr!.gradle -xr!.idea -xr!build

:Exit
pause