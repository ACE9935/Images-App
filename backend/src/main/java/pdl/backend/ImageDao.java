package pdl.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public class ImageDao implements Dao<Image> {

private final Map<Long, Image> images = new HashMap<>();

@Override
public Optional<Image> retrieve(final long id) {
    // Assuming 'images' is a Map<Long, Image>
    Image img = images.get(id);  // Retrieve the image by ID
    return Optional.ofNullable(img);  // Wrap the result in an Optional
}

  @Override
  public List<Image> retrieveAll() {
  
    return new ArrayList<Image>(images.values());
  }

  @Override
  public void create(final Image img) {
    images.put(img.getId(), img);
  }

  @Override
  public void update(final Image img, final String[] params) {
    // Not used
  }

  @Override
  public void delete(final Image img) {
    images.remove(img.getId());
  }
}
