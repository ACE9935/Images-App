package pdl.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;

import java.io.IOException;
import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ImageRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public void setInitialImageIndexes(List<ImageIndex> imageIndexes) {
    try {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS images ("
                + "id SERIAL PRIMARY KEY, "
                + "width INT, "
                + "height INT, "
                + "name TEXT, "
                + "format TEXT, "
                + "histogram_of_visual_words BYTEA, "
                + "histogram_2d BYTEA, "
                + "histogram_3d BYTEA"
                + ")";
        jdbcTemplate.update(createTableQuery);

        // Use TRUNCATE instead of DELETE to reset ID sequence
        String truncateQuery = "TRUNCATE TABLE images RESTART IDENTITY";
        jdbcTemplate.update(truncateQuery);

        for (ImageIndex imageIndex : imageIndexes) {
            String insertQuery = "INSERT INTO images (width, height, name, format, histogram_of_visual_words, histogram_2d, histogram_3d) VALUES (?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(insertQuery, 
                imageIndex.getWidth(), 
                imageIndex.getHeight(), 
                imageIndex.getName(),
                imageIndex.getFormat(),
                DBUtils.serialize1D(imageIndex.getHistogramOfVisualWords()),
                DBUtils.serialize2D(imageIndex.getHistogram2D()),
                DBUtils.serialize2D(imageIndex.getHistogram3D())
            );
        }
    } catch (DataAccessException | IOException e) {
        e.printStackTrace();
    }
}



public ImageMetadata getImageMetaData(long id) {

        try {
         String query = "SELECT id, width, height, name, format FROM images WHERE id = ?";
         ImageMetadata imageMetaData = jdbcTemplate.queryForObject(query, new Object[]{id}, new RowMapper<ImageMetadata>() {
            @Override
            public ImageMetadata mapRow(ResultSet rs, int rowNum) throws SQLException {
                long imageId = rs.getLong("id");
                int width = rs.getInt("width");
                int height = rs.getInt("height");
                String name = rs.getString("name");
                String format = rs.getString("format");

                return new ImageMetadata(imageId, width, height, name, format);
            }
        });

        return imageMetaData;

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

public void addImageMetaData(ImageIndex imageIndex) {

        try {
         String insertQuery = "INSERT INTO images (id, width, height, name, format, histogram_of_visual_words, histogram_2d, histogram_3d) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                jdbcTemplate.update(insertQuery, 
                    imageIndex.getId(), 
                    imageIndex.getWidth(), 
                    imageIndex.getHeight(), 
                    imageIndex.getName(),
                    imageIndex.getFormat(),
                    DBUtils.serialize1D(imageIndex.getHistogramOfVisualWords()),
                    DBUtils.serialize2D(imageIndex.getHistogram2D()),
                    DBUtils.serialize2D(imageIndex.getHistogram3D())
            );

        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (DataAccessException e) {
            e.printStackTrace();
        }

}

public void deleteImageIndex(long id) {

        try {

         String query = "DELETE FROM images WHERE id = ?";
         jdbcTemplate.update(query, id);

        } 
        catch (EmptyResultDataAccessException e) {
          System.out.println("No image found with the specified ID.");
        } 
        catch (DataAccessException e) {
          e.printStackTrace();
        }

}

public List<ImageMetadata> getAllImagesMetaData() {
    try {
        String query = "SELECT id, width, height, name, format FROM images";

        List<ImageMetadata> imageMetaDataList = jdbcTemplate.query(query, new RowMapper<ImageMetadata>() {
            @Override
            public ImageMetadata mapRow(ResultSet rs, int rowNum) throws SQLException {
                long imageId = rs.getLong("id");
                int width = rs.getInt("width");
                int height = rs.getInt("height");
                String name = rs.getString("name");
                String format = rs.getString("format");

                return new ImageMetadata(imageId, width, height, name, format);
            }
        });

        return imageMetaDataList;

    } catch (DataAccessException e) {
        e.printStackTrace();
        return new ArrayList<>();
    }
}

public List<ImageMetadata> getSortedImagesMetaIndexes(long id, int N, String descr) {
    
    try {
        String query = "SELECT id, width, height, name, format, " + descr + " FROM images";

        List<ImageIndex> imageList = jdbcTemplate.query(query, new RowMapper<ImageIndex>() {
            @Override
            public ImageIndex mapRow(ResultSet rs, int rowNum) throws SQLException {
                long imageId = rs.getLong("id");
                int width = rs.getInt("width");
                int height = rs.getInt("height");
                String name = rs.getString("name");
                String format = rs.getString("format");
                byte[] histo = rs.getBytes(descr);

                try {
                    Object histogram = descr.equals("histogram_2d") ? DBUtils.deserialize2D(histo) :
                                       descr.equals("histogram_3d") ? DBUtils.deserialize2D(histo) :
                                       DBUtils.deserialize1D(histo);
                    return new ImageIndex(imageId, name, width, height, format, descr, histogram);
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

                ImageMetadata metadata = new ImageMetadata(img.getId(), img.getWidth(), img.getHeight(), img.getName(), img.getFormat());
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


}
