#!/bin/bash

#Change to the correct directory if the script is linked to!
SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
  DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
cd $DIR

#Start CyDIW
java -Xms128m -Xmx512m -classpath `java cysystem.printclasspath.SystemConfigClasspath < SystemConfig.xml` cysystem.diwGUI.gui.DBGui
