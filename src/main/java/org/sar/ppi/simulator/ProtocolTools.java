package org.sar.ppi.simulator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.sar.ppi.PpiException;


import java.io.*;
import java.util.*;


public class ProtocolTools {


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
        List<Object[]> func_list = new ArrayList<>();
        try (FileReader f = new FileReader(path)){
            JSONObject jsonObject = (JSONObject) parser.parse(f);
            //nb call protocole
            int nb_call = jsonObject.size();
            for (int i = 0;i<nb_call;i++){
                func_list.add(ProtocolFromJSON((JSONObject) jsonObject.get("Call_"+(i+1))));
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return func_list;
    }


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


       /*  
  public static void main(String[] args) {
        //FileWriter file = new FileWriter(new File("/home/adrien/output.json"));
        try(
            FileReader filer = new FileReader("/home/adrien/output.json");) {
         List<Object> array = new ArrayList<>();
            array.add("arg_1");
            array.add(1);
            JSONObject jo = new JSONObject();
                 protocolToJSON("App",5,4,array);
            jo.put("call1",1);
          file.write(protocolToJSON("App",4,3,array).toString());


            JSONParser parser = new JSONParser();
            JSONObject ob = (JSONObject) parser.parse(filer);

            Object[] array = ProtocolFromJSON (ob);
            System.out.println(Arrays.toString(array));
            //Integer i =  Integer.parseInt(o.get("call1").toString());
            //Object ob = i;



            //System.out.println(o.get("call1").toString());
        } catch (IOException | ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
    }
*/



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
