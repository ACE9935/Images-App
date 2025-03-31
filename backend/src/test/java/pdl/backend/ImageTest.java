package pdl.backend;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ImageTest {

    @Test
    public void testImageCreation() {
        byte[] data = new byte[] { 0x1, 0x2, 0x3 };
        Image image = new Image(data);

        assertNotNull(image.getId());
        assertArrayEquals(data, image.getData());
    }

    @Test
    public void testImageIdIncrement() {
        Image.resetCount(); 
        Image image1 = new Image(new byte[] { 0x1 });
        Image image2 = new Image(new byte[] { 0x2 });

        assertEquals(1, image1.getId());
        assertEquals(2, image2.getId());
    }
}