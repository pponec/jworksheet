#!/bin/sh
# ===================================================================
# runJ4Lin.sh version 0.94 - general starting script for Linux or Unix.
#
# Copyright (C) 2003-2006 Pavel Ponec, e-mail:ppsee2@gmail.com
# Home Page: http://ponec.net/ppsee/runj4/index.htm
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU Lesser General Public License as published by
# the Free Software Foundation version 2 of the License.
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY.
# Please read bin/license-lgpl.txt for the full details. A copy of the LGPL may 
# be found at http://www.gnu.org/licenses/lgpl.txt .
#
# A History:
# rel. 0.92 - 2003/11/30 - The first public version under GPL License
# rel. 0.94 - 2006/02/02 - Released under LGPL License
#
# Usage: /bin/sh jWorkSheet.sh
# ===================================================================

# Modify path to file "java" in case you can't run the application.
java="" # "/usr/lib/jre1.5.0_06/bin/java"

# Maximal Memory Alocation:
parameter="-Xmx180m -Xms32m" 

# A Java Archive:
jarName="jWorkSheet.jar"

# User Parameters:
paru=""

# === CORE: ===
javax="$java"
if [ ! -f "${javax}" ]; then
  javax=${JAVA_HOME}/bin/java  
  if [ ! -f "$javax" ]; then
    echo "ERROR in \"ppsee.sh\" script:"
    echo "File \"$java\" was not found."
    echo "Ensure you have installed a Java environment"
    echo "or try to modify a \"java\" variable in the current script."
    echo "See \"read-me.html\" file for more information."
    exit 1
  fi
fi
path=`echo "$0" | sed "s/[^/]*$//"`
"$javax" $parameter -jar "$path$jarName" $@
# === EOF ===
