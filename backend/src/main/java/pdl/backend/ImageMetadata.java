package pdl.backend;

public class ImageMetadata {

    private long id;
    private int width;
    private int height;
    private String name;
    private String format;
    private double score;

    public ImageMetadata(long id, int width, int height, String name, String format) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.name = name;
        this.format = format;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public void setScore(double score) {
      this.score = score;
    }

    public double getScore() {
        return score;
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getName() {
        return name;
    }

    public String getFormat() {
        return format;
    }

}
