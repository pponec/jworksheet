#!/bin/sh
# Install the jWorkSheet to the Ubuntu:

set -e
targetDir=$HOME/opt
runnerScript=jws.sh

# The Ujorm 3 builds its metamodel at the run time by the Java compiler,
# so a JDK is required - a plain JRE is not enough:
if ! command -v javac > /dev/null 2>&1; then
    echo "ERROR: A JDK 17 or newer is required, but no 'javac' was found." >&2
    echo "       The jWorkSheet builds its data metamodel at the run time" >&2
    echo "       using the Java compiler, so a plain JRE is not enough." >&2
    echo "       Install it for example by: sudo apt install default-jdk" >&2
    exit 1
fi

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

