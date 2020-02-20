#!/bin/bash

if [[ -z $2 ]]
then
    echo "usage:"
    echo "./mpirun <nb-process> <class> [<args>]"
    exit
fi
nb_proc=$1
shift
class=$1
shift
build_dir=$(pwd)/target
classpath=$build_dir/classes:$build_dir/test-classes:$(cat $build_dir/classpath.txt)
mpirun --oversubscribe -np $nb_proc java -cp $classpath org.sar.ppi.$class $*