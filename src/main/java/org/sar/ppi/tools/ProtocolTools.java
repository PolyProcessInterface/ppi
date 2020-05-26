package org.sar.ppi.tools;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.sar.ppi.PpiException;


import java.io.*;
import java.util.*;


/**
 * ProtocolTools class.
 */
public class ProtocolTools {
    /**
     *
     * @param node
     *  The node to turn off
     * @param start_at
     *  The delay to wait before the start
     * @return
     */
    public static JSONObject BreakDownToJSON(int node, long start_at){
        JSONObject jo = new JSONObject();
        jo.put("node",node);
        jo.put("start",start_at);
        return jo;
    }

    /**
     *
     * @param o
     * Json object "on" or "off"
     * @param name
     * "on" or "off"
     * @return
     */
    public static List<Object[]> BreakDownFromJSON(JSONObject o,String name){
        JSONArray ja = (JSONArray) o.get(name);
        JSONObject jo;
        List<Object[]>retArray = new ArrayList<>();
        if(ja==null)
            return retArray;
        Object[] tuple;
        for(int i = 0,len = ja.size();i<len;i++){
            jo= (JSONObject) ja.get(i);
            tuple = new Object[2];
            tuple[0]=Integer.parseInt(jo.get("node").toString());
            tuple[1]=jo.get("start");
            retArray.add(tuple);
        }
        return retArray;
    }


    /**
     * Write a call to a function to a Json Object
     *
     * @param funcName
     *     The name of the function that gone be called
     * @param node
     *      The node on which she is gonne bee called
     * @param delay
     *       The delay to wait before the call
     * @param args
     *       The args
     * @return a {@link org.json.simple.JSONObject} object.
     */
    public static JSONObject CallFuncToJSON(String funcName , int node, long delay, List<Object> args){
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

    /**
     * Transform a {@link org.json.simple.JSONObject} object into an
     * {@link java.lang.Object} array protocl.
     *
     * @param o a {@link org.json.simple.JSONObject} object.
     * @return the protocol.
     */
    public static Object[] CallFuncFromJSON(JSONObject o){
        JSONArray ja = (JSONArray) o.get("args");
        int len=ja.size()+3;
        Object [] object_func = new Object[len];
        object_func[0]=o.get("FunctionName");
        object_func[1]=Integer.parseInt(o.get("Node").toString());
        object_func[2]=o.get("Delay");
        for(int i =3,indexJa =0;i<len;i++,indexJa++){
            JSONObject jo = (JSONObject) ja.get(indexJa);
            String type = (String) jo.get("type");
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

    /**
     *
     * @param path
     * Path to the Json file
     * @return
     * returns "on" "off" or "events" list of calls
     */
    public static HashMap<String,List<Object[]>> readProtocolJSON(String path){
        HashMap<String,List<Object[]>> res = null;
        JSONParser parser = new JSONParser();
        List<Object[]> func_list = new ArrayList<>();
        try (FileReader f = new FileReader(path)){

            JSONObject jsonObject = (JSONObject) parser.parse(f);
            //nb call protocole
            JSONArray ja = (JSONArray) jsonObject.get("events");
            if(ja!=null)
                for(Object o : ja){
                    Object[] recip = CallFuncFromJSON((JSONObject) o);
                    func_list.add(recip);
                }
            res = new HashMap<>();
            res.put("events",func_list);
            res.put("Off",BreakDownFromJSON(jsonObject,"Off"));
            res.put("On",BreakDownFromJSON(jsonObject,"On"));


        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return res;
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
     * @throws java.io.IOException on io failure
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
