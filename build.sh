#!/bin/sh
#
# Usage: sh /fullpath/build.sh

set -e
PDIR=$(dirname $0)
# export JAVA_HOME=/opt/java/corretto17

cd $PDIR
sh ./mvnw clean install -T 4 --also-make