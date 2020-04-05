package org.sar.ppi;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class ProtocolTools {

    /**
     *
     * @param funcName
     *     The name of the function that gone be called
     * @param node
     *      The node on which she is gonne bee called
     * @param delay
     *       The delay to wait before the call
     * @param args
     *       The args
     * @param str
     *      The OutputStream of the file where to write
     * @throws IOException
     *
     */
    public static void writeTimeFuncCall(String funcName , int node,long delay, List<Object> args ,ObjectOutputStream str) throws IOException {
        str.writeUTF(funcName);
        str.writeInt(node);
        str.writeInt(args.size());
        str.writeLong(delay);
        for(Object o : args)
            str.writeObject(o);
        str.flush();
    }

    /**
     *
     * @param b
     * @return Object[]
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object[] readTimeFuncCall(ObjectInputStream b) throws IOException, ClassNotFoundException {
        String name = b.readUTF();
        int node = b.readInt();
        int nb_arg = b.readInt();
        Object [] object_func = new Object[nb_arg+3];
        object_func[0]=name;
        object_func[1]=node;
        object_func[2]=b.readLong();
        for(int i =3;i<nb_arg+3;i++)
            object_func[i]=b.readObject();
        return object_func;
    }

}
