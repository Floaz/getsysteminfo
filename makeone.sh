#!/bin/bash
#
# This tool generates a self extractable archive of the program.
# You need MAKESELF (http://megastep.org/makeself/) and UNZIP.
#
# 1) Run 'gradle distZip'
# 2) Run this script.
# 3) The result is build/distributions/getsysteminfo.sh
# 
# Copyright (c) 2013 Philipp René Thomas <thomas@rfh-koeln.de>
#



# Change the location of MAKESELF:
SCRIPT='/home/'$USER'/scripts/programs/makeself-2.1.5/makeself.sh'






DISTDIR=./build/distributions/
MAKESELFDIR=./build/distributions/makeself

echo "Create makeself build dir..."
mkdir $MAKESELFDIR
cd $MAKESELFDIR

echo "Unzip distribution..."
unzip ../GetSystemInfo.zip
cd ../../../

echo "Make self extractable archive..."
$SCRIPT $MAKESELFDIR $DISTDIR'getsysteminfo.sh' 'GetSystemInfo' './GetSystemInfo/bin/GetSystemInfo' 

echo "Finish!"

