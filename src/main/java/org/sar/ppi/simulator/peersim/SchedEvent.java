package org.sar.ppi.simulator.peersim;

public class SchedEvent {
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
    public Object clone() {
        return new SchedEvent(Funcname,args.clone());
    }


}
