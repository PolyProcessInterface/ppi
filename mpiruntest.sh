#!/bin/bash

if [[ -z $2 ]]
then
    echo "usage:"
    echo "./mpirun <nb-process> <class> [<mpi-options>]"
    exit
fi
nb_proc=$1
shift
class=$1
shift
classpath_file=target/classpath.txt
mvn dependency:build-classpath -Dmdep.includeScope=compile -Dmdep.outputFile=$classpath_file
classpath=$(pwd)/target/classes:$(pwd)/target/test-classes:$(cat $classpath_file)
cd target/test-classes
mpirun --oversubscribe -np $nb_proc $* java -cp $classpath org.sar.ppi.$class