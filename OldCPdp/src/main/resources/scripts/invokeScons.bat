@echo off

echo Probing for scons...
call scons --version 1>NUL 2> NUL
if errorlevel 1 goto :noScons

echo scons is installed...
echo build procedure not yet finished... just copying existing libraries
xcopy /Y /E ..\..\..\nativeLibs ..\..\..\target\classes\nativeLibs
pause
exit

:noScons
	echo scons is NOT installed; skipping build
	mkdir ..\..\..\target\classes\nativeLibs
	xcopy /Y /E ..\..\..\nativeLibs ..\..\..\target\classes\nativeLibs
	
	pause
