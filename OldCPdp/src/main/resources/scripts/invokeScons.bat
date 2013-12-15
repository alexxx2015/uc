@echo off

echo Probing for scons...
call scons --version 1>NUL 2> NUL
if errorlevel 1 goto :noScons

echo scons is installed...
echo build procedure not yet finished...
pause
exit

:noScons
	echo scons is NOT installed; skipping build
	pause
	