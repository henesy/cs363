rem Customize this sample file to your system before using it 
rem remark out echo and pause commands when no longer needed 
echo off


rem Section 1. Setting environment variables
rem ----------------------------------------
echo * 
echo Trying to set environment variables ...
pause

rem setting path variable 
rem The setting may be different on your computer 
set PATH=C:\Program Files\Java\jdk1.7.0_10\bin; 

rem setting classpath variable, including current directory (.): 
rem The setting may be different on your computer 
set CLASSPATH=C:\Program Files\Java\jdk1.7.0_10\lib;


rem Section 2. Compiling a demo java program
rem ----------------------------------------
echo * 
echo Trying to compile the demo java program ...
pause 
javac -d ../../../ -cp ../../../;lib/dom4j-2.0.0-ALPHA-2.jar;lib/saxon9ee.jar; *.java

Pause 
