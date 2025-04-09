package pdl.backend;

import java.io.IOException;
import java.time.Instant;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;

public class ImageIndex extends ImageMetadata {

    private static Long count = Long.valueOf(1);
    private int[] histogram_of_visual_words;
    private int[][] histogram_2d;
    private int[][] histogram_3d;

    public ImageIndex(byte[] imgData, final String imgUrl, final String name, final int width, final int height, final String format, final String imgClass, final float imgClassConfidence, final Instant imageUploadDate, final String author, final int likeCount, final int downloadCount, final String title) {
        super(count++, imgUrl, width, height, name, format, imgClass, imgClassConfidence, imageUploadDate, author, likeCount, downloadCount, title);

        try {
            Planar<GrayU8> image = ImageUtils.loadImage(imgData);
            GrayU8 grayImage = ImageUtils.loadImageGray(imgData);

            this.histogram_of_visual_words = SimilarityComputing.computeHistogram(ImageUtils.extractFeatures(ImageUtils.resizeImageNearest(grayImage, 32, 32)));
            this.histogram_2d = ImageUtils.histogramHueSaturation(image);
            this.histogram_3d = ImageUtils.histogramOfRGB(image);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ImageIndex(long id, final String imgUrl, final String name, final int width, final int height, final String format, final String descr, final Object histogram, final String imgClass, final float imgClassConfidence, final Instant imageUploadDate, final String author, final int likeCount, final int downloadCount, final String title) {
        // Use the provided id instead of generating a new one
        super(id, imgUrl, width, height, name, format, imgClass, imgClassConfidence, imageUploadDate, author, likeCount, downloadCount, title);

        if (descr.equals("histogram_of_visual_words")) {
            this.histogram_of_visual_words = (int[]) histogram;
        } else if (descr.equals("histogram_3d")) {
            this.histogram_3d = (int[][]) histogram;
        } else if (descr.equals("histogram_2d")) {
            this.histogram_2d = (int[][]) histogram;
        }
    }

    public int[] getHistogramOfVisualWords() {
        return histogram_of_visual_words;
    }

    public int[][] getHistogram2D() {
        return histogram_2d;
    }

    public int[][] getHistogram3D() {
        return histogram_3d;
    }
}

