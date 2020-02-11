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
cd target/classes
mpirun --oversubscribe -np $nb_proc $* java org/sar/ppi/$class