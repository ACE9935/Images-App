package pdl.backend;

public class ImageIndex extends ImageMetadata {

    private static Long count = Long.valueOf(0);
    private int[][] histogram_2d;
    private int[][][] histogram_3d;

    public ImageIndex(final String name, final int width, final int height, final String format, final int[][] data_2d, final int[][][] data_3d) {

        super(count++, width, height, name, format);

        this.histogram_2d = data_2d;
        this.histogram_3d = data_3d;
    }

    public ImageIndex(long id, final String name, final int width, final int height, final String format, final int[][] data_2d, final int[][][] data_3d) {

        super(id, width, height, name, format);

        this.histogram_2d = data_2d;
        this.histogram_3d = data_3d;
    }

    public int[][] getHistogram2D() {
        return histogram_2d;
    }

    public int[][][] getHistogram3D() {
        return histogram_3d;
    }
}

