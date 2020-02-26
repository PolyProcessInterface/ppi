package org.sar.ppi;

import java.io.*;

public class ContentHandler {
    // juste des teste
    public static <T extends Serializable> byte[] ParseObject(T message) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos);) {
            out.writeObject(message);
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new PpiException("ERROR OF PARSING");
    }

    public static  byte[] ParseObject(Object message) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos);) {
            out.writeObject(message);
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new PpiException("ERROR OF PARSING");
    }

    public static Object RetriveObject(byte[] message) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(message);
             ObjectInput in = new ObjectInputStream(bis);) {
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new PpiException("ERROR OF PARSING");
    }

}
