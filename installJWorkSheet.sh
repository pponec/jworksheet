#!/bin/sh
# Installations to the new Ubuntu:

set -e


# jWorkSheet
file=jworkhseet.zip
mkdir -p ~/bin && cd ~/bin
wget -O $file https://github.com/pponec/jworksheet/archive/refs/heads/master.zip
unzip -o $file
rm $file
cd jworksheet-master
./mvnw install -DskipTests

