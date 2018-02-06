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
set CLASSPATH=C:\Program Files\Java\jdk1.7.0_10\lib;.;


rem Section 2. Compiling a demo java program
rem ----------------------------------------
echo * 
echo Trying to compile the demo java program ...
pause 
javac JDBC_Demo.java 


rem Section 3. Executing the demo java program
rem ------------------------------------------
echo * 
echo Trying to execute the demo java program ...
pause 
java -cp ".;mysql-connector-java-5.0.5.jar;" JDBC_Demo > JDBC_Demo_Output.txt


echo Finishing execution of this batch file ... 
Pause 
