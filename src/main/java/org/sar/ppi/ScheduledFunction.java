package org.sar.ppi;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.TimerTask;

/**
 * ScheduledFunction class.
 */
public class ScheduledFunction extends TimerTask implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private Object[] args;
    private NodeProcess node;

    /**
     * Constructor for ScheduledFunction.
     *
     * @param funcName a {@link java.lang.String} object.
     * @param args an array of {@link java.lang.Object} objects.
     * @param node a {@link org.sar.ppi.NodeProcess} object.
     */
    public ScheduledFunction(String funcName , Object[] args , NodeProcess node){
        name=funcName;
        this.args=args;
        this.node=node;
    }

    /** {@inheritDoc} */
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
