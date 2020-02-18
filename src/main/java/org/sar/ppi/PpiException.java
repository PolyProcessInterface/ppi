package org.sar.ppi;

import mpi.MPIException;

/**
 * PpiException
 */
public class PpiException extends RuntimeException {

    /**
     * Version
     */
    private static final long serialVersionUID = 1L;

    public PpiException(MPIException e) {
        super(e);
    }

    public PpiException(String msg, MPIException e) {
        super(msg, e);
    }

}