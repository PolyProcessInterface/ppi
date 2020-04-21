package org.sar.ppi;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.TimerTask;

public class ScheduledFunction extends TimerTask implements Serializable {
    private String name;
    private Object[] args;
    private NodeProcess node;

    public ScheduledFunction(String funcName , Object[] args , NodeProcess node){
        name=funcName;
        this.args=args;
        this.node=node;
    }

    @Override
    public void run() {
        for(Method m : node.getClass().getMethods()){
            if(m.getName().equals(name))
                try {
                    System.out.println("j invoque "+name +" args = "+ Arrays.toString(args)+"n= "+node);

                    m.invoke(node,args);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
        }
    }
}
