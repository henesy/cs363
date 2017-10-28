cd %~dp0

rem pause
for /f "usebackq tokens=*" %%i in (`java cysystem.printclasspath.SystemConfigClasspath SystemConfig.xml`) do java -Xms128m -Xmx512m -classpath "%%i" cysystem.diwGUI.gui.DBGui
pause
