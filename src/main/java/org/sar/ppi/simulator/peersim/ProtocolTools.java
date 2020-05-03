package org.sar.ppi.simulator.peersim;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.sar.ppi.PpiException;


import java.io.*;
import java.util.*;


public class ProtocolTools {

    /**
     * Write a call to a function to a Json Object
     * @param funcName
     *     The name of the function that gone be called
     * @param node
     *      The node on which she is gonne bee called
     * @param delay
     *       The delay to wait before the call
     * @param args
     *       The args
     * @return
     *       Json Object
     */
    public static JSONObject protocolToJSON(String funcName , int node,long delay, List<Object> args){
        JSONObject jo = new JSONObject();
        JSONArray ja = new JSONArray();
        jo.put("FunctionName",funcName);
        jo.put("Node",node);
        jo.put("Delay",delay);
        for(int i=0,len =args.size();i<len;i++){
            JSONObject arg = new JSONObject();
            arg.put("type",getType(args.get(i)));
            arg.put("val",args.get(i));
            ja.add(arg);
        }
        jo.put("args",ja);
        return jo;
    }

    public static Object[] ProtocolFromJSON(JSONObject o){
        JSONArray ja = (JSONArray) o.get("args");
        int len=ja.size()+3;
        Object [] object_func = new Object[len];
        object_func[0]=o.get("FunctionName");
        object_func[1]=Integer.parseInt(o.get("Node").toString());
        object_func[2]=o.get("Delay");
        for(int i =3,indexJa =0;i<len;i++,indexJa++){
            JSONObject jo = (JSONObject) ja.get(indexJa);
            String type = (String) jo.get("type");
            //ptet ajouter par la suite
            switch(type) {
                case "Integer":
                    object_func[i]=Integer.parseInt(jo.get("val").toString());
                    break;
                default:
                    object_func[i] = jo.get("val");
            }

        }
        return object_func;
    }

    public static List<Object[]> readProtocolJSON(String path){
        JSONParser parser = new JSONParser();
        System.out.println("je suis passer par la fun2");
        List<Object[]> func_list = new ArrayList<>();
        System.out.println("je suis passer par la fun22");
        try (FileReader f = new FileReader(path)){

            JSONObject jsonObject = (JSONObject) parser.parse(f);
            //nb call protocole
            int nb_call = jsonObject.size();
            for (int i = 0;i<nb_call;i++){
                System.out.println("i="+i);
                Object[] recip =ProtocolFromJSON((JSONObject) jsonObject.get("Call_"+(i+1)));
                System.out.println(Arrays.toString(recip));
                func_list.add(recip);

            }
            System.out.println("je suis passer par la fun23 + "+nb_call);
        } catch (IOException | ParseException e) {
            System.out.println("error");
            e.printStackTrace();
        }

        return func_list;
    }

    /** Json object joins some types (Integer/long)  */
    private static String getType(Object o ){
        if(o instanceof Integer)
            return "Integer";
        if(o instanceof String)
            return "String";
        if(o instanceof Byte)
            return "Byte";
        if(o instanceof Boolean)
            return "Boolean";
        if(o instanceof Double)
            return "Double";
        throw new PpiException("ERROR : Type not Alowed");
    }

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
    public static void writeTimeFuncCall(String funcName , int node, long delay, List<Object> args , ObjectOutputStream str) throws IOException {
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
