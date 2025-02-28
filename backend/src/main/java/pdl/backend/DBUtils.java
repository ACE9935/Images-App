package pdl.backend;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

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

    public static int[][] deserialize2D(byte[] bytes, int width, int height) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int[][] histogram = new int[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                histogram[i][j] = buffer.getInt();
            }
        }
        return histogram;
    }

    public static int[][][] deserialize3D(byte[] bytes, int xBins, int yBins, int zBins) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int[][][] histogram = new int[xBins][yBins][zBins];

        for (int i = 0; i < xBins; i++) {
            for (int j = 0; j < yBins; j++) {
                for (int k = 0; k < zBins; k++) {
                    histogram[i][j][k] = buffer.getInt();
                }
            }
        }
        return histogram;
    }
}

