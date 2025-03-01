package pdl.backend;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.nio.BufferUnderflowException;

public class DBUtils {

public static byte[] serialize2D(int[][] histogram) throws IOException {
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
        objectOutputStream.writeObject(histogram);
        return byteArrayOutputStream.toByteArray();
    }
}

public static byte[] serialize3D(int[][][] histogram) throws IOException {
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
        objectOutputStream.writeObject(histogram);
        return byteArrayOutputStream.toByteArray();
    }
}

    public static int[][] deserialize2D(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            return (int[][]) objectInputStream.readObject();
        }
    }

    public static int[][][] deserialize3D(byte[] bytes) throws IOException, ClassNotFoundException{
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            return (int[][][]) objectInputStream.readObject();
        }
    }
}

