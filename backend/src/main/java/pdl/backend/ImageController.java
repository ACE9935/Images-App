package pdl.backend;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
public class ImageController {

  @Autowired
  private ObjectMapper mapper;

  private final ImageDao imageDao;

  @Autowired
  public ImageController(ImageDao imageDao) {
    this.imageDao = imageDao;
  }

  @RequestMapping(value = "/images/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
  public ResponseEntity<?> getImage(@PathVariable("id") long id) {

   Optional<Image> optionalImage = imageDao.retrieve(id);

    if (optionalImage.isPresent()) {
        Image imgFile = optionalImage.get();
        byte[] imageData = imgFile.getData();
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageData); 
    } else {
        return new ResponseEntity<>("Image not found", HttpStatus.NOT_FOUND);
    }

  }



@RequestMapping(value = "/images/{id}", method = RequestMethod.DELETE)
public ResponseEntity<?> deleteImage(@PathVariable("id") long id) {
 Optional<Image> imageOptional = imageDao.retrieve(id);
 if (imageOptional.isPresent()) {
 imageDao.delete(imageOptional.get());
 return new ResponseEntity<>("Image deleted successfully", HttpStatus.OK);
 } else {
 return new ResponseEntity<>("Image not found", HttpStatus.NOT_FOUND);
 }
}

@RequestMapping(value = "/images", method = RequestMethod.POST)
public ResponseEntity<?> addImage(@RequestParam("file") MultipartFile file,
                                  RedirectAttributes redirectAttributes) {

    if (file.isEmpty()) {
        return new ResponseEntity<>("File is empty", HttpStatus.NOT_FOUND);
    }

    try {
        Image newImage = new Image(file.getOriginalFilename(), file.getBytes());
        imageDao.create(newImage);
    } catch (IOException e) {
        // Handle the exception and return an error response
        return new ResponseEntity<>("Failed to read file bytes", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<>("Image added successfully", HttpStatus.OK);
}

  @RequestMapping(value = "/images", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
  @ResponseBody
  public ArrayNode getImageList() {
        ArrayNode nodes = mapper.createArrayNode(); // Create a JSON array

        // Retrieve all images from the DAO
        List<Image> images = imageDao.retrieveAll();

        // Populate the JSON array with image data
        for (Image image : images) {
            ObjectNode node = mapper.createObjectNode();
            node.put("id", image.getId());
            node.put("name", image.getName());
            node.put("data", image.getData()); // Example: add a size property
            nodes.add(node);
        }

        return nodes; // Return the populated JSON array
    }

}
