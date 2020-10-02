#!/bin/sh

#drop into src
cd src
#find everything in src and pass it all to javac, redirecting the classes into their respective package in bin
find -name "*.java" -exec javac -cp "../lib/jcurses.jar" -d "../bin" {} + &&
#create the jar with only the manifest
jar -cfm ../Main.jar ../manifest.mf &&
#switch over into bin
cd ../bin &&
#find everything under bin and add it all to the jar
find -name "*.class" -exec jar -uf "../Main.jar" {} + &&
#hop back out
cd .. &&
#delete all the old class files
rm -r bin/** &&
#make the jar executable
chmod +x Main.jar
