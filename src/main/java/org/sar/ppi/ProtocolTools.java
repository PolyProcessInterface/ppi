package org.sar.ppi;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public static JSONObject ProtocolToJSON(String funcName , int node,long delay, List<Object> args){
        JSONObject jo = new JSONObject();
        JSONArray ja = new JSONArray();
        jo.put("FunctionName",funcName);
        jo.put("Node",node);
        jo.put("Delay",delay);
        Map m = new LinkedHashMap(args.size());
        for(Object o :args)
            m.put(getType(o),o);
        ja.add(m);
        jo.put("args",ja);
        return jo;
    }

    private static String getType(Object o ){
        if(o instanceof Integer)
            return "Integer";
        if(o instanceof String)
            return "String";
        if(o instanceof Byte)
            return "Byte";
        throw new PpiException("ERROR : Type not Alowed");
    }

  /*  public static void main(String[] args) {
        try(FileWriter file = new FileWriter(new File("/home/adrien/output.json"));) {
            List<Object> array = new ArrayList<>();
            array.add("arg_1");
            array.add(1);
            file.write(ProtocolToJSON("App",5,4,array).toJSONString());
            file.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
    }*/
}
