package pdl.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import java.io.IOException;
import org.springframework.stereotype.Repository;
import pdl.backend.DBUtils;
import pdl.backend.ImageIndex;
import pdl.backend.ImageMetadata;
import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

@Repository
public class ImageRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

public void setInitialImageIndexes(List<ImageIndex> imageIndexes) {
    for (ImageIndex imageIndex : imageIndexes) {
        try {
            String checkExistenceQuery = "SELECT COUNT(*) FROM images WHERE id = ?";
            int count = jdbcTemplate.queryForObject(checkExistenceQuery, Integer.class, imageIndex.getId());

            if (count > 0) {
                
                String updateQuery = "UPDATE images SET width = ?, height = ?, name = ?, format=?, histogram_2d = ?, histogram_3d = ? WHERE id = ?";
                jdbcTemplate.update(updateQuery, 
                    imageIndex.getWidth(),
                    imageIndex.getHeight(),
                    imageIndex.getName(),
                    imageIndex.getFormat(),
                    DBUtils.serialize2D(imageIndex.getHistogram2D()), 
                    DBUtils.serialize3D(imageIndex.getHistogram3D()), 
                    imageIndex.getId());
            } else {
                
                String insertQuery = "INSERT INTO images (id, width, height, name, format, histogram_2d, histogram_3d) VALUES (?, ?, ?, ?, ?, ?, ?)";
                jdbcTemplate.update(insertQuery, 
                    imageIndex.getId(), 
                    imageIndex.getWidth(), 
                    imageIndex.getHeight(), 
                    imageIndex.getName(),
                    imageIndex.getFormat(),
                    DBUtils.serialize2D(imageIndex.getHistogram2D()), 
                    DBUtils.serialize3D(imageIndex.getHistogram3D()));
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    try {
        
        String deleteUnusedIndexes = "DELETE FROM images WHERE id > ?";
        jdbcTemplate.update(deleteUnusedIndexes, imageIndexes.get(imageIndexes.size() - 1).getId());
    } catch (DataAccessException e) {
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
         String insertQuery = "INSERT INTO images (id, width, height, name, format, histogram_2d, histogram_3d) VALUES (?, ?, ?, ?, ?, ?, ?)";
                jdbcTemplate.update(insertQuery, 
                    imageIndex.getId(), 
                    imageIndex.getWidth(), 
                    imageIndex.getHeight(), 
                    imageIndex.getName(),
                    imageIndex.getFormat(),
                    DBUtils.serialize2D(imageIndex.getHistogram2D()), 
                    DBUtils.serialize3D(imageIndex.getHistogram3D()));

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
            String query = "SELECT id, width, height, name, format, histogram_2d, histogram_3d FROM images";

            List<ImageIndex> imageList = jdbcTemplate.query(query, new RowMapper<ImageIndex>() {
                @Override
                public ImageIndex mapRow(ResultSet rs, int rowNum) throws SQLException {
                    long imageId = rs.getLong("id");
                    int width = rs.getInt("width");
                    int height = rs.getInt("height");
                    String name = rs.getString("name");
                    String format = rs.getString("format");

                    byte[] hist2DBytes = descr.equals("histogram_2d") ? rs.getBytes("histogram_2d") : null;
                    byte[] hist3DBytes = descr.equals("histogram_3d") ? rs.getBytes("histogram_3d") : null;

                    int[][] hist2D = hist2DBytes != null ? DBUtils.deserialize2D(hist2DBytes, 256, 256) : null;
                    int[][][] hist3D = hist3DBytes != null ? DBUtils.deserialize3D(hist3DBytes, 16, 16, 16) : null;

                    return new ImageIndex(name, width, height, format, hist2D, hist3D);
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
                .sorted(Comparator.comparingDouble(img -> {
                    if (descr.equals("histogram_2d")) {
                        return ImageUtils.euclideanDistance2D(targetImage.getHistogram2D(), img.getHistogram2D());
                    } else {
                        return ImageUtils.euclideanDistance3D(targetImage.getHistogram3D(), img.getHistogram3D());
                    }
                }))
                .limit(N) 
                .map(img -> new ImageMetadata(img.getId(), img.getWidth(), img.getHeight(), img.getName(), img.getFormat())) // Remove histograms
                .collect(Collectors.toList());

            return sortedImages;

        } catch (DataAccessException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


}
