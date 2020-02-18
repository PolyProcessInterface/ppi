package org.sar.ppi;

import mpi.MPI;
import mpi.MPIException;
import mpi.Status;

/**
 * MpiInfrastructure
 */
public class MpiInfrastructure extends Infrastructure {

    public MpiInfrastructure(Protocol protocol) {
        super(protocol);
    }

    @Override
    public void run(String[] args) throws PpiException {
        try {
            MPI.Init(args);
            while (true) {
                Object buf = new Object();
                Status status = MPI.COMM_WORLD.recv(buf, 1, MPI.INT, MPI.ANY_SOURCE, MPI.ANY_TAG);
                protocol.processMessage(new Node(status.getSource()), buf);
            }
        } catch (MPIException e) {
            throw new PpiException("Init fail.", e);
        }
    }

    @Override
    public void send(Node dest, Object message) {
        // TODO Auto-generated method stub
        
    }
}