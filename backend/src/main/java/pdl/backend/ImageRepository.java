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
                // Update the existing record with the new fields
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
                // Insert a new record with all fields
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
        return new ArrayList<>(); // Return an empty list in case of failure
    }
}


}
