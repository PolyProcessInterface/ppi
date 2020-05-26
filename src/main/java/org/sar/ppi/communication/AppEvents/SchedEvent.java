package org.sar.ppi.communication.AppEvents;

import org.sar.ppi.peersim.PeerSimInfrastructure;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SchedEvent implements AppEvent {
    private Object[] args;
    private String Funcname ;
    private PeerSimInfrastructure peerInfra;
    public SchedEvent(String name , Object[] args , PeerSimInfrastructure p) {
        this.Funcname=name;
        this.args=args;
        peerInfra=p;
    }
    public Object[] getArgs() {
        return args;
    }

    public String getFuncName() {
        return Funcname;
    }

    @Override
    public Object clone() {
        return new SchedEvent(Funcname,args.clone(), (PeerSimInfrastructure) peerInfra.clone());
    }


    @Override
    public void run() {
        for(Method m : peerInfra.getProcess().getClass().getMethods()){
            if(m.getName().equals(Funcname)) {
                peerInfra.serialThreadRun(() -> {
                    try {
                        m.invoke(peerInfra.getProcess(),args);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}

