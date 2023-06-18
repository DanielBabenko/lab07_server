package server.manager;

import server.object.LabWork;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;

public class SerializationManager {

    public static byte[] serialize(LinkedList<?> collection) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);

        out.writeObject(collection);

        return bos.toByteArray();
    }


    public static HashSet<LabWork> deserialize(byte[] byteArray) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
        ObjectInputStream ois = new ObjectInputStream(bis);

        return (HashSet<LabWork>) ois.readObject();
    }

    public static LabWork deserializeObject(byte[] byteArray) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
        ObjectInputStream ois = new ObjectInputStream(bis);

        return (LabWork) ois.readObject();
    }
}
