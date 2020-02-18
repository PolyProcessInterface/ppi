package org.sar.ppi;

import mpi.Comm;
import mpi.MPI;
import mpi.MPIException;
import mpi.Status;

/**
 * MpiInfrastructure
 */
public class MpiInfrastructure extends Infrastructure {

    protected boolean running = true;
    protected Comm comm;

    public MpiInfrastructure(Protocol protocol) {
        super(protocol);
    }

    @Override
    public void run(String[] args) throws PpiException {
        try {
            MPI.Init(args);
            comm = MPI.COMM_WORLD;
            currentNode = new MpiNode(comm.getRank());
            protocol.startNode(currentNode);
            while (running) {
                Object buf = new Object();
                Status status = comm.recv(buf, 1, MPI.INT, MPI.ANY_SOURCE, MPI.ANY_TAG);
                protocol.processMessage(new MpiNode(status.getSource()), buf);
            }
            MPI.Finalize();
        } catch (MPIException e) {
            throw new PpiException("Init fail.", e);
        }
    }

    @Override
    public void send(Node dest, Object message) {
        // TODO Auto-generated method stub
    }

    @Override
    public void broadcast(Object messsage) {
        // TODO Auto-generated method stub
    }

    @Override
    public void exit() {
        running = false;
    }

    @Override
    public Node getNode(int id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int size() {
        try {
            return comm.getSize();
        } catch (MPIException e) {
            throw new PpiException("Fail to get size.", e);
        }
    }
}