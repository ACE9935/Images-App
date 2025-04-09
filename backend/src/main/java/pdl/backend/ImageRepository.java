package pdl.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.io.IOException;
import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ImageRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public void setInitialImageIndexes() {
    try {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS images ("
                + "id SERIAL PRIMARY KEY, "
                + "width INT, "
                + "height INT, "
                + "name TEXT, "
                + "format TEXT, "
                + "histogram_of_visual_words BYTEA, "
                + "histogram_2d BYTEA, "
                + "histogram_3d BYTEA,"
                + "img_class TEXT, "
                + "img_class_confidence FLOAT, "
                + "img_url TEXT, "
                + "img_upload_date TIMESTAMPTZ DEFAULT NOW(), "
                + "author TEXT, "
                + "like_count INT DEFAULT 0, "
                + "download_count INT DEFAULT 0, "
                + "title TEXT"
                + ")";
        jdbcTemplate.update(createTableQuery);

    } catch (DataAccessException e) {
        e.printStackTrace();
    }
}

public ImageMetadata getImageMetaData(long id) {
    try {
        String query = "SELECT id, width, height, name, format, img_class, img_class_confidence, img_url, img_upload_date, author, like_count, download_count, title FROM images WHERE id = ?";
        
        return jdbcTemplate.queryForObject(query, new Object[]{id}, new RowMapper<ImageMetadata>() {
            @Override
            public ImageMetadata mapRow(ResultSet rs, int rowNum) throws SQLException {
                long imageId = rs.getLong("id");
                int width = rs.getInt("width");
                int height = rs.getInt("height");
                String name = rs.getString("name");
                String format = rs.getString("format");
                String imgClass = rs.getString("img_class");
                float imgClassConfidence = rs.getFloat("img_class_confidence");
                String imgUrl = rs.getString("img_url");
                String author = rs.getString("author");
                int likeCount = rs.getInt("like_count");
                int downloadCount = rs.getInt("download_count");
                String title = rs.getString("title");

                // Retrieve img_upload_date as Timestamp and convert to Instant
                Timestamp timestamp = rs.getTimestamp("img_upload_date");
                Instant imgUploadDate = (timestamp != null) ? timestamp.toInstant() : null;

                return new ImageMetadata(imageId, imgUrl, width, height, name, format, imgClass, imgClassConfidence, imgUploadDate, author, likeCount, downloadCount, title);
            }
        });
    } 
    catch (EmptyResultDataAccessException e) {
        System.out.println("No image found with the specified ID.");
        return null;
    } 
    catch (DataAccessException e) {
        e.printStackTrace();
        return null;
    }
}

public long addImageMetaData(ImageIndex imageIndex) {
    try {
        String insertQuery = "INSERT INTO images (width, height, name, format, histogram_of_visual_words, histogram_2d, histogram_3d, img_class, img_class_confidence, img_url, author, title) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Prepare the KeyHolder to capture the auto-generated key
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            // Create the prepared statement with the insert query
            var preparedStatement = connection.prepareStatement(insertQuery, new String[]{"id"}); // "id" is the auto-generated key
            preparedStatement.setInt(1, imageIndex.getWidth());
            preparedStatement.setInt(2, imageIndex.getHeight());
            preparedStatement.setString(3, imageIndex.getName());
            preparedStatement.setString(4, imageIndex.getFormat());
            
            // Wrap the serialization methods that might throw IOException inside try-catch
            try {
                preparedStatement.setBytes(5, DBUtils.serialize1D(imageIndex.getHistogramOfVisualWords()));
                preparedStatement.setBytes(6, DBUtils.serialize2D(imageIndex.getHistogram2D()));
                preparedStatement.setBytes(7, DBUtils.serialize2D(imageIndex.getHistogram3D()));
            } catch (IOException e) {
                e.printStackTrace();
                return null; 
            }

            preparedStatement.setString(8, imageIndex.getImgClass());
            preparedStatement.setDouble(9, imageIndex.getImgClassConfidence());
            preparedStatement.setString(10, imageIndex.getImgUrl());
            preparedStatement.setString(11, imageIndex.getAuthor());
            preparedStatement.setString(12, imageIndex.getTitle());
            return preparedStatement;
        }, keyHolder); 

        // Return the generated ID from the KeyHolder
        return keyHolder.getKey().longValue();

    } catch (DataAccessException e) {
        e.printStackTrace();
        return -1;
    }
}


public void deleteImageIndex(long id) {
    try {
        String query = "DELETE FROM images WHERE id = ?";
        jdbcTemplate.update(query, id);

    } catch (EmptyResultDataAccessException e) {
        System.out.println("No image found with the specified ID.");
    } catch (DataAccessException e) {
        e.printStackTrace();
    }
}

public List<ImageMetadata> getAllImagesMetaData() {
    try {
        String query = "SELECT id, width, height, name, format, img_class, img_class_confidence, img_url, img_upload_date, author, like_count, download_count, title FROM images";

        return jdbcTemplate.query(query, new RowMapper<ImageMetadata>() {
            @Override
            public ImageMetadata mapRow(ResultSet rs, int rowNum) throws SQLException {
                long imageId = rs.getLong("id");
                int width = rs.getInt("width");
                int height = rs.getInt("height");
                String name = rs.getString("name");
                String format = rs.getString("format");
                String imgClass = rs.getString("img_class");
                float imgClassConfidence = rs.getFloat("img_class_confidence");
                String imgUrl = rs.getString("img_url");
                String author = rs.getString("author");
                int likeCount = rs.getInt("like_count");
                int downloadCount = rs.getInt("download_count");
                String title = rs.getString("title");

                // Retrieve img_upload_date as Timestamp and convert to Instant
                Timestamp timestamp = rs.getTimestamp("img_upload_date");
                Instant imgUploadDate = (timestamp != null) ? timestamp.toInstant() : null;

                return new ImageMetadata(imageId, imgUrl, width, height, name, format, imgClass, imgClassConfidence, imgUploadDate, author, likeCount, downloadCount, title);
            }
        });

    } catch (DataAccessException e) {
        e.printStackTrace();
        return new ArrayList<>(); // Return an empty list in case of an error
    }
}

public List<ImageMetadata> getSortedImagesMetaIndexes(long id, int N, String descr) {
    
    try {
        String query = "SELECT id, width, height, name, format, img_class, img_class_confidence, img_url, img_upload_date, author, like_count, download_count, title, " + descr + " FROM images";

        List<ImageIndex> imageList = jdbcTemplate.query(query, new RowMapper<ImageIndex>() {
            @Override
            public ImageIndex mapRow(ResultSet rs, int rowNum) throws SQLException {
                long imageId = rs.getLong("id");
                int width = rs.getInt("width");
                int height = rs.getInt("height");
                String name = rs.getString("name");
                String format = rs.getString("format");
                String imgClass = rs.getString("img_class");
                float imgClassConfidence = rs.getFloat("img_class_confidence");
                String imgUrl = rs.getString("img_url");
                byte[] histo = rs.getBytes(descr);
                Timestamp timestamp = rs.getTimestamp("img_upload_date");
                Instant imgUploadDate = (timestamp != null) ? timestamp.toInstant() : null;
                String author = rs.getString("author");
                int likeCount = rs.getInt("like_count");
                int downloadCount = rs.getInt("download_count");
                String title = rs.getString("title");

                try {
                    Object histogram = descr.equals("histogram_2d") ? DBUtils.deserialize2D(histo) :
                                       descr.equals("histogram_3d") ? DBUtils.deserialize2D(histo) :
                                       DBUtils.deserialize1D(histo);
                    return new ImageIndex(imageId, imgUrl, name, width, height, format, descr, histogram, imgClass, imgClassConfidence, imgUploadDate, author, likeCount, downloadCount, title);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });

        ImageIndex targetImage = imageList.stream()
            .filter(img -> img.getId() == id)
            .findFirst()
            .orElse(null);

        if (targetImage == null) {
            System.out.println("Image with ID " + id + " not found.");
            return new ArrayList<>();
        }

        List<ImageMetadata> sortedImages = imageList.stream()
            .filter(img -> img.getId() != id)
            .map(img -> {
                double distance = 0.0;
                if (descr.equals("histogram_of_visual_words")) {
                    distance = ImageUtils.euclideanDistance1D(targetImage.getHistogramOfVisualWords(), img.getHistogramOfVisualWords());
                } else if (descr.equals("histogram_2d")) {
                    distance = ImageUtils.euclideanDistance2D(targetImage.getHistogram2D(), img.getHistogram2D());
                } else if (descr.equals("histogram_3d")) {
                    distance = ImageUtils.euclideanDistance2D(targetImage.getHistogram3D(), img.getHistogram3D());
                } else {
                    throw new IllegalArgumentException("Unsupported histogram type: " + descr);
                }

                ImageMetadata metadata = new ImageMetadata(img.getId(), img.getImgUrl(), img.getWidth(), img.getHeight(), img.getName(), img.getFormat(), img.getImgClass(), img.getImgClassConfidence(), img.getImgUploadDate(), img.getAuthor(), img.getLikeCount(), img.getDownloadCount(), img.getTitle());   
                metadata.setScore(1 / (1 + distance));
                return new AbstractMap.SimpleEntry<>(distance, metadata);
            })
            .sorted(Comparator.comparingDouble(Map.Entry::getKey))
            .limit(N) 
            .map(Map.Entry::getValue)
            .collect(Collectors.toList());

        return sortedImages;

    } catch (DataAccessException e) {
        e.printStackTrace();
        return new ArrayList<>();
    }
}

public List<ImageMetadata> getImagesByClassAndOrderByConfidence(String imgClass) {
    try {
        // SQL query to retrieve images with the same img_class and order them by img_class_confidence in descending order
        String query = "SELECT id, width, height, name, format, img_class, img_class_confidence, img_url, img_upload_date, author, like_count, download_count, title FROM images WHERE img_class = ? ORDER BY img_class_confidence DESC";

        // Execute the query
        List<ImageMetadata> images = jdbcTemplate.query(query, new Object[]{imgClass}, new RowMapper<ImageMetadata>() {
            @Override
            public ImageMetadata mapRow(ResultSet rs, int rowNum) throws SQLException {
                long imageId = rs.getLong("id");
                int width = rs.getInt("width");
                int height = rs.getInt("height");
                String name = rs.getString("name");
                String format = rs.getString("format");
                String imgClass = rs.getString("img_class");
                float imgClassConfidence = rs.getFloat("img_class_confidence");
                String imgUrl = rs.getString("img_url");
                String author = rs.getString("author");
                int likeCount = rs.getInt("like_count");
                int downloadCount = rs.getInt("download_count");
                String title = rs.getString("title");

                // Retrieve img_upload_date as Timestamp and convert to Instant
                Timestamp timestamp = rs.getTimestamp("img_upload_date");
                Instant imgUploadDate = (timestamp != null) ? timestamp.toInstant() : null;

                return new ImageMetadata(imageId, imgUrl, width, height, name, format, imgClass, imgClassConfidence, imgUploadDate, author, likeCount, downloadCount, title);
            }
        });

        return images; // Return the list of images sorted by img_class_confidence

    } catch (DataAccessException e) {
        e.printStackTrace();
        return new ArrayList<>(); // Return an empty list in case of an error
    }
}

public void incrementLikeCount(long id) {
    try {
   
        String query = "UPDATE images SET like_count = like_count + 1 WHERE id = ?";
        
        // Execute the update query
        int rowsUpdated = jdbcTemplate.update(query, id);

        if (rowsUpdated > 0) {
            System.out.println("Like count incremented successfully.");
        } else {
            System.out.println("No image found with the specified ID to increment the like count.");
        }
    } catch (DataAccessException e) {
        e.printStackTrace();
    }
}

public void decrementLikeCount(long id) {
    try {

        String query = "UPDATE images SET like_count = GREATEST(like_count - 1, 0) WHERE id = ?";
        
        // Execute the update query
        int rowsUpdated = jdbcTemplate.update(query, id);

        if (rowsUpdated > 0) {
            System.out.println("Like count decremented successfully.");
        } else {
            System.out.println("No image found with the specified ID to decrement the like count.");
        }
    } catch (DataAccessException e) {
        e.printStackTrace();
    }
}

public void incrementDownloadCount(long id) {
    try {
        String query = "UPDATE images SET download_count = download_count + 1 WHERE id = ?";
        int rowsUpdated = jdbcTemplate.update(query, id);

        if (rowsUpdated > 0) {
            System.out.println("Download count incremented successfully.");
        } else {
            System.out.println("No image found with the specified ID to increment the download count.");
        }
    } catch (DataAccessException e) {
        e.printStackTrace();
    }
}

public void updateImageTitle(long id, String newTitle) {
    try {
        String query = "UPDATE images SET title = ? WHERE id = ?";
        int rowsUpdated = jdbcTemplate.update(query, newTitle, id);

        if (rowsUpdated > 0) {
            System.out.println("Image title updated successfully.");
        } else {
            System.out.println("No image found with the specified ID to update the title.");
        }
    } catch (DataAccessException e) {
        e.printStackTrace();
    }
}

}
