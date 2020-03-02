package org.sar.ppi.mpi;

import java.io.*;

import org.sar.ppi.Message;
import org.sar.ppi.PpiException;

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

    public static byte[] ParseMessage(Message message) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos);) {
            out.writeObject(message);
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new PpiException("ERROR OF PARSING");
    }

    public static Message RetriveMessage(byte[] message) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(message);
             ObjectInput in = new ObjectInputStream(bis);) {
            return (Message) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new PpiException("ERROR OF PARSING");
    }

}
