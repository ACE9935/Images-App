package pdl.backend;

import boofcv.abst.feature.dense.DescribeImageDense;
import boofcv.alg.color.ColorHsv;
import boofcv.factory.feature.dense.ConfigDenseSurfStable;
import boofcv.factory.feature.dense.FactoryDescribeImageDense;
import boofcv.struct.feature.TupleDesc_F64;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import boofcv.io.image.ConvertBufferedImage;

import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageUtils {

    public static Planar<GrayU8> loadImage(byte[] imageBytes) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
        BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);

        Planar<GrayU8> image = new Planar<>(GrayU8.class, bufferedImage.getWidth(), bufferedImage.getHeight(), 3);
        ConvertBufferedImage.convertFrom(bufferedImage, image, true);
        return image;
    }

    public static GrayU8 loadImageGray(byte[] imageBytes) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
        BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);

        Planar<GrayU8> image = new Planar<>(GrayU8.class, bufferedImage.getWidth(), bufferedImage.getHeight(), 3);
        ConvertBufferedImage.convertFrom(bufferedImage, image, true);
        return image.getBand(0);
    }

    public static int[][] histogramOfRGB(Planar<GrayU8> image) {
        int width = image.width;
        int height = image.height;
    
        int[][] histogram = new int[3][256];
    
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int r = image.getBand(0).get(x, y);
                int g = image.getBand(1).get(x, y);
                int b = image.getBand(2).get(x, y);
    
                histogram[0][r]++;
                histogram[1][g]++;
                histogram[2][b]++;
            }
        }
    
        return histogram;
    }

    public static int[][] histogramHueSaturation(Planar<GrayU8> image) {
        int width = image.width;
        int height = image.height;
    
        int[][] histogram = new int[360][100];
    
        float[] hsv = new float[3];
    
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int r = image.getBand(0).get(x, y);
                int g = image.getBand(1).get(x, y);
                int b = image.getBand(2).get(x, y);

                ColorHsv.rgbToHsv(r, g, b, hsv);

                int pixelHue = (int) hsv[0];
                int pixelSaturation = (int) (hsv[1] * 99);
    
                histogram[pixelHue][pixelSaturation]++;
            }
        }
    
        return histogram;
    }

    public static GrayU8 resizeImageNearest(GrayU8 input, int newWidth, int newHeight) {
        GrayU8 resized = new GrayU8(newWidth, newHeight);
        for (int y = 0; y < newHeight; y++) {
            int srcY = y * input.height / newHeight;
            for (int x = 0; x < newWidth; x++) {
                int srcX = x * input.width / newWidth;
                resized.set(x, y, input.get(srcX, srcY));
            }
        }
        return resized;
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

    public static double euclideanDistance2D(int[][] hist1, int[][] hist2) {
        double sum = 0.0;
        for (int i = 0; i < hist1.length; i++) {
            for (int j = 0; j < hist1[i].length; j++) {
                sum += Math.pow(hist1[i][j] - hist2[i][j], 2);
            }
        }
        return Math.sqrt(sum);
    }

    public static double euclideanDistance3D(int[][][] hist1, int[][][] hist2) {
        double sum = 0.0;
        for (int i = 0; i < hist1.length; i++) {
            for (int j = 0; j < hist1[i].length; j++) {
                for (int k = 0; k < hist1[i][j].length; k++) {
                    sum += Math.pow(hist1[i][j][k] - hist2[i][j][k], 2);
                }
            }
        }
        return Math.sqrt(sum);
    }

    public static double euclideanDistance1D(int[] histogram1, int[] histogram2) {

        double sum = 0.0;
        for (int i = 0; i < histogram1.length; i++) {
            sum += Math.pow(histogram1[i] - histogram2[i], 2);
        }

        return Math.sqrt(sum);
    }

    public static List<TupleDesc_F64> extractFeatures(GrayU8 grayImage) {

        List<TupleDesc_F64> descriptors = new ArrayList<>();
    
        // Configure Dense SURF with finer sampling
        ConfigDenseSurfStable config = new ConfigDenseSurfStable();
        config.descriptorScale = 0.5; // Adjust based on desired descriptor size
        config.sampling.periodX = 2;  // Reduce to increase keypoint density
        config.sampling.periodY = 2;  // Reduce to increase keypoint density
        config.surf.widthLargeGrid = 4;
        
        // Extract dense SURF features from the grayscale image
        DescribeImageDense<GrayU8, TupleDesc_F64> denseExtractor = 
            FactoryDescribeImageDense.surfStable(config, GrayU8.class);
        
        denseExtractor.process(grayImage);
        descriptors.addAll(denseExtractor.getDescriptions());
    
        return descriptors;
    }
    

}
