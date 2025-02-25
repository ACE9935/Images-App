package pdl.backend;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

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

}
