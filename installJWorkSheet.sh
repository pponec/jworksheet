#!/bin/sh
# Install the jWorkSheet to the Ubuntu:

set -e
targetDir=$HOME/opt
runnerScript=jws.sh

# jWorkSheet
file=jworkhseet.zip
mkdir -p $targetDir && cd $targetDir
wget -O $file https://github.com/pponec/jworksheet/archive/refs/heads/master.zip
unzip -o $file
rm $file
cd jworksheet-master
./mvnw install -DskipTests

# Create a runner script:
echo "#!/bin/sh\njava -jar $(ls $PWD/target/*ies.jar)" > ../$runnerScript
chmod 755 ../$runnerScript

