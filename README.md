# Parallel Programing Interface

## OPENMI java

### Install

```bash
wget https://download.open-mpi.org/release/open-mpi/v4.0/openmpi-4.0.2.tar.gz
tar zxvf openmpi-4.0.2.tar.gz
cd openmpi-4.0.2
./configure --enable-mpi-java --with-jdk-bindir=$JAVA_HOME/bin/ --with-jdk-headers=$JAVA_HOME/include
make -j2
sudo make install
sudo ldconfig
cd -
```

### Ring implementation

```java
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
```

### compile

    mvn install

### run

    ./mpirun.sh 6 RingMpi

## Peersim

### Install

```bash
wget http://downloads.sourceforge.net/project/peersim/peersim-1.0.5.zip
unzip peersim-1.0.5.zip
sudo cp peersim-1.0.5/*.jar /usr/local/lib
```
