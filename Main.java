import mpi.Comm;
import mpi.MPI;
import mpi.MPIException;
import mpi.Status;

public class Main {

    public static void main(String[] args) {
        try {
            MPI.Init(args);
            Comm comm = MPI.COMM_WORLD;
            int size = comm.getSize();
            int rank = comm.getRank();
            int neighbour = (rank + 1) % size;
            int hellotag = 1;
            Integer msg = 0;
            Status status;
            if (rank == 0) {
                comm.send(msg, 0, MPI.INT, neighbour, hellotag);
                status = comm.recv(msg, 0, MPI.INT, MPI.ANY_SOURCE, hellotag);
            } else {
                status = comm.recv(msg, 0, MPI.INT, MPI.ANY_SOURCE, hellotag);
                comm.send(msg, 0, MPI.INT, neighbour, hellotag);
            }
            System.out.println(rank + " Received hello from " + status.getSource());
            MPI.Finalize();
        } catch (MPIException e) {
            e.printStackTrace();
        }
    }
}