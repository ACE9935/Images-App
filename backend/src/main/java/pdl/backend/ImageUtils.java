package pdl.backend;

import boofcv.alg.color.ColorHsv;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import boofcv.io.image.ConvertBufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageUtils {

    public static int[] histogramOfHues(Planar<GrayU8> image) {
        int width = image.width;
        int height = image.height;

        int[] histogram = new int[360];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int r = image.getBand(0).get(x, y);
                int g = image.getBand(1).get(x, y);
                int b = image.getBand(2).get(x, y);

                float[] hsv = new float[3];
                ColorHsv.rgbToHsv(r, g, b, hsv);
                int pixelHue = (int) hsv[0];

                histogram[pixelHue]++;
            }
        }
        return histogram;
    }

    public static Planar<GrayU8> loadImage(byte[] imageBytes) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
        BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);

        Planar<GrayU8> image = new Planar<>(GrayU8.class, bufferedImage.getWidth(), bufferedImage.getHeight(), 3);
        ConvertBufferedImage.convertFrom(bufferedImage, image, true);
        return image;
    }

    public static int[][][] histogramOfRGB(Planar<GrayU8> image) {
        int width = image.width;
        int height = image.height;

        int[][][] histogram = new int[256][256][256];

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int r = image.getBand(0).get(x, y);
                int g = image.getBand(1).get(x, y);
                int b = image.getBand(2).get(x, y);

                histogram[r][g][b]++;
            }
        }
        return histogram;
    }

    public static int[][] histogramHueSaturation(Planar<GrayU8> image) {
        int width = image.width;
        int height = image.height;

        int[][] histogram = new int[360][100];

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int r = image.getBand(0).get(x, y);
                int g = image.getBand(1).get(x, y);
                int b = image.getBand(2).get(x, y);

                float[] hsv = new float[3];
                ColorHsv.rgbToHsv(r, g, b, hsv);
                int pixelHue = (int) hsv[0];
                int pixelSaturation = (int) (hsv[1] * 99); // Scale saturation to 0-99

                histogram[pixelHue][pixelSaturation]++;
            }
        }
        return histogram;
    }

    public static boolean compare2DHistograms(int[][] hist1, int[][] hist2) {
        for (int i = 0; i < hist1.length; i++) {
            for (int j = 0; j < hist1[i].length; j++) {
                if (hist1[i][j] != hist2[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean compare3DHistograms(int[][][] hist1, int[][][] hist2) {
        for (int i = 0; i < hist1.length; i++) {
            for (int j = 0; j < hist1[i].length; j++) {
                for (int k = 0; k < hist1[i][j].length; k++) {
                    if (hist1[i][j][k] != hist2[i][j][k]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static void deleteImageFromImagesFolder(String filename) {
        try {
            Path imagePath = Paths.get("resources/images/" + filename); 
            Files.deleteIfExists(imagePath);
            System.out.println(filename+" deleted successfully from /images.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error while deleting the image.");
        }
    }
}
