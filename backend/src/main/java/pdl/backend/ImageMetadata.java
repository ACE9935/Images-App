package pdl.backend;

import java.time.Instant;

public class ImageMetadata {

    private long id;
    private int width;
    private int height;
    private String name;
    private String title;
    private String format;
    private double score;
    private String imgClass;
    private float imgClassConfidence;
    private String imgUrl;
    private Instant imgUploadDate;
    private String author;
    private int likeCount;
    private int downloadCount;

    public ImageMetadata(long id, String imgUrl,  int width, int height, String name, String format, String imgClass, float imgClassConfidence, Instant imgUploadDate, String author, int likeCount, int downloadCount, String title) {
        this.id = id;
        this.title = title;
        this.width = width;
        this.height = height;
        this.name = name;
        this.format = format;
        this.imgClass=imgClass;
        this.imgUrl=imgUrl;
        this.imgClassConfidence=imgClassConfidence;
        this.imgUploadDate = imgUploadDate;
        this.author = author;
        this.likeCount = likeCount;
        this.downloadCount = downloadCount;
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

    public String getImgClass() {
        return imgClass;
    }

    public float getImgClassConfidence() {
        return imgClassConfidence;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public Instant getImgUploadDate() {
        return imgUploadDate;
    }

    public String getAuthor() {
        return author;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public int getDownloadCount() {
        return downloadCount;
    }
    
    public String getTitle() {
        return title;
    }
}
