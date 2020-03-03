package org.sar.ppi;

import mpi.Comm;
import mpi.MPI;
import mpi.MPIException;
import mpi.Status;


public class NaimiTrehelMpi {

    private static void simulateur( Comm comm) throws MPIException {
        int i;
        int TAGINIT=0;
        int NB_SITE=6;

        /* nb_voisins[i] est le nombre de voisins du site i */
        int nb_voisins[] = {-1,5, 1, 1, 1, 1, 1, 1};

        /* liste des voisins */
        int voisins[][] = {{-1, -1, -1, -1, -1},{2, 3, 4, 5,6},
            {1, -1, -1, -1, -1}, {1, -1, -1, -1, -1},
            {1, -1, -1, -1, -1}, {1, -1, -1, -1, -1},
            {1, -1, -1, -1, -1}, {1, -1, -1, -1, -1}};

        boolean jeton = true;

        for(i=1; i<=NB_SITE; i++){
            if(jeton) {
                //jeton que pour le A
                comm.send(jeton, 1, MPI.BOOLEAN, i, MPI.ANY_TAG);
                jeton=false;
            }else
                comm.send(jeton, 1, MPI.BOOLEAN, i, MPI.ANY_TAG);
            comm.send(nb_voisins[i], 1, MPI.INT, i, MPI.ANY_TAG);
            comm.send(voisins[i], nb_voisins[i], MPI.INT, i, MPI.ANY_TAG);
        }
    }
    public static void main(String[] args) {
        try {
            MPI.Init(args);
            Comm comm = MPI.COMM_WORLD;
            int size = comm.getSize();
            int rank = comm.getRank();
            int neighbour = (rank + 1) % size;
            int hellotag = 1;
            Integer msg = 0;
            Status status=null;
            if (rank == 0) {
                //Initialization
                simulateur(comm);
            } else {


            }
        //    System.out.println(rank + " Received hello from " + status.getSource());
            MPI.Finalize();
        } catch (MPIException e) {
            e.printStackTrace();
        }
    }
}

