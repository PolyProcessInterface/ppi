package org.sar.ppi.peersim;

import peersim.core.Node;
import peersim.edsim.EDProtocol;

public class SchedEvent implements EDProtocol {
    private Object[] args;
    private String Funcname ;
    public SchedEvent(String name , Object[] args) {
        this.Funcname=name;
        this.args=args;
    }
    public Object[] getArgs() {
        return args;
    }

    public String getFuncName() {
        return Funcname;
    }

    @Override
    public void processEvent(Node node, int i, Object o) {
        System.out.println("IZAAAAAAAA");
    }

    @Override
    public Object clone() {
        return null;
    }
}
