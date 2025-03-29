package pdl.backend;

import org.junit.jupiter.api.Test;
import pdl.backend.DBUtils;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.Arrays;

public class DBUtilsTest {

    @Test
    void testSerializeDeserialize1D() throws IOException, ClassNotFoundException {
        int[] original = {1, 2, 3, 4, 5};
        byte[] serialized = DBUtils.serialize1D(original);
        int[] deserialized = DBUtils.deserialize1D(serialized);
        assertArrayEquals(original, deserialized);
    }

    @Test
    void testSerializeDeserialize2D() throws IOException, ClassNotFoundException {
        int[][] original = {{1, 2, 3}, {4, 5, 6}};
        byte[] serialized = DBUtils.serialize2D(original);
        int[][] deserialized = DBUtils.deserialize2D(serialized);
        assertArrayEquals(original, deserialized);
    }

    @Test
    void testSerializeDeserialize3D() throws IOException, ClassNotFoundException {
        int[][][] original = {{{1, 2}, {3, 4}}, {{5, 6}, {7, 8}}};
        byte[] serialized = DBUtils.serialize3D(original);
        int[][][] deserialized = DBUtils.deserialize3D(serialized);
        assertArrayEquals(original, deserialized);
    }

    @Test
    void testInvalidDeserialization() {
        byte[] invalidData = new byte[]{1, 2, 3, 4};
        assertThrows(IOException.class, () -> DBUtils.deserialize2D(invalidData));
        assertThrows(IOException.class, () -> DBUtils.deserialize3D(invalidData));
        assertThrows(IOException.class, () -> DBUtils.deserialize1D(invalidData));
    }
}
