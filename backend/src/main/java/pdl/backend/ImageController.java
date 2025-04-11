package pdl.backend;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.dao.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    private ImageRepository imageRepository;

    @Autowired
    public ImageController(ImageRepository imageRepository){
        this.imageRepository = imageRepository;
        imageRepository.setInitialImageIndexes();
    }

    @RequestMapping(value = "/images/{id}", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public ResponseEntity<?> getImageMetaData(@PathVariable("id") long id) {
        try {
            ImageMetadata imageMetaData = imageRepository.getImageMetaData(id);

            if (imageMetaData != null) {
                ObjectNode node = mapper.createObjectNode();
                node.put("id", imageMetaData.getId());
                node.put("name", imageMetaData.getName());
                node.put("format", imageMetaData.getFormat());
                node.put("size", imageMetaData.getWidth() + "x" + imageMetaData.getHeight());
                node.put("imgClass", imageMetaData.getImgClass());
                node.put("imgClassConfidence", imageMetaData.getImgClassConfidence()); // Now float
                node.put("imgUrl", imageMetaData.getImgUrl());
                node.put("author", imageMetaData.getAuthor());
                node.put("likeCount", imageMetaData.getLikeCount());
                node.put("downloadCount", imageMetaData.getDownloadCount());
                node.put("title", imageMetaData.getTitle());

                Instant uploadDate = imageMetaData.getImgUploadDate();
                if (uploadDate != null) {
                    String formattedDate = DateTimeFormatter.ISO_INSTANT.format(uploadDate);
                    node.put("imgUploadDate", formattedDate);
                } else {
                    node.putNull("imgUploadDate");
                }

                return new ResponseEntity<>(node, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Image not found", HttpStatus.NOT_FOUND);
            }

        } catch (DataAccessException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error accessing the database", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/images/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteImage(@PathVariable("id") long id) {
        try {
            ImageMetadata imageMetaData = imageRepository.getImageMetaData(id);

            if (imageMetaData != null) {
                imageRepository.deleteImageIndex(id);
                return new ResponseEntity<>("Image deleted successfully from DB", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Image not found", HttpStatus.NOT_FOUND);
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error accessing the database", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/images", method = RequestMethod.POST)
    public ResponseEntity<?> addImage(@RequestParam("file") MultipartFile file,
                                      @RequestParam("imgUrl") String imgUrl,
                                      @RequestParam("author") String author,
                                      @RequestParam("title") String title,
                                      RedirectAttributes redirectAttributes) {
    
        if (file.isEmpty()) {
            return new ResponseEntity<>("File is empty", HttpStatus.BAD_REQUEST);
        }
    
        String filename = file.getOriginalFilename();
        String contentType = file.getContentType();
    
        if (contentType == null || 
            (!filename.endsWith(".jpg") && !filename.endsWith(".png") && !filename.endsWith(".jpeg"))) {
            return new ResponseEntity<>("Unsupported media type", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }
    
        try {
            byte[] fileBytes = file.getBytes();
    
            Planar<GrayU8> image = ImageUtils.loadImage(fileBytes);
    
            ModelPredictor classifier = new ModelPredictor();
            String[] predictedClassAndItsProbability = classifier.classifyImage(fileBytes);
    
            // Handle confidence parsing: Replace comma with dot before parsing
            String confidenceStr = predictedClassAndItsProbability[1].replace(',', '.');
            float imgClassConfidence = Float.parseFloat(confidenceStr); // Now parsing the confidence correctly
    
            Instant timestamp = Instant.now();
    
            ImageIndex imageIndex = new ImageIndex(fileBytes, imgUrl, filename, image.getWidth(), image.getHeight(),
                                                  contentType, predictedClassAndItsProbability[0], imgClassConfidence, timestamp, author, 0, 0, title);
    
            long newImageId=imageRepository.addImageMetaData(imageIndex);
    
            ObjectNode node = mapper.createObjectNode();
            node.put("id", newImageId);
            node.put("name", imageIndex.getName());
            node.put("format", imageIndex.getFormat());
            node.put("size", imageIndex.getWidth() + "x" + imageIndex.getHeight());
            node.put("author", imageIndex.getAuthor());
            node.put("title", imageIndex.getTitle());
    
            return new ResponseEntity<>(node, HttpStatus.OK);
    
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to save image", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    

    @RequestMapping(value = "/images", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    @ResponseBody
    public ArrayNode getImageList() {
        List<ImageMetadata> images = imageRepository.getAllImagesMetaData();
        ArrayNode nodes = mapper.createArrayNode();

        for (ImageMetadata image : images) {
            ObjectNode node = mapper.createObjectNode();
            node.put("id", image.getId());
            node.put("name", image.getName());
            node.put("type", image.getFormat());
            node.put("size", image.getWidth() + "x" + image.getHeight());
            node.put("imgClass", image.getImgClass());
            node.put("imgClassConfidence", image.getImgClassConfidence()); // Now float
            node.put("imgUrl", image.getImgUrl());
            node.put("author", image.getAuthor());
            node.put("likeCount", image.getLikeCount());
            node.put("downloadCount", image.getDownloadCount());
            node.put("title", image.getTitle());

            Instant uploadDate = image.getImgUploadDate();
            if (uploadDate != null) {
                String formattedDate = DateTimeFormatter.ISO_INSTANT.format(uploadDate);
                node.put("imgUploadDate", formattedDate);
            } else {
                node.putNull("imgUploadDate");
            }

            nodes.add(node);
        }

        return nodes;
    }

    @RequestMapping(value = "/images/class/{class_x}", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    @ResponseBody
    public ArrayNode getImageClassList(@PathVariable("class_x") String imgClass) {
        List<ImageMetadata> images = imageRepository.getImagesByClassAndOrderByConfidence(imgClass);
        ArrayNode nodes = mapper.createArrayNode();

        for (ImageMetadata image : images) {
            ObjectNode node = mapper.createObjectNode();
            node.put("id", image.getId());
            node.put("name", image.getName());
            node.put("type", image.getFormat());
            node.put("size", image.getWidth() + "x" + image.getHeight());
            node.put("imgClass", image.getImgClass());
            node.put("imgClassConfidence", image.getImgClassConfidence()); // Now float
            node.put("imgUrl", image.getImgUrl());
            node.put("author", image.getAuthor());
            node.put("likeCount", image.getLikeCount());
            node.put("downloadCount", image.getDownloadCount());
            node.put("title", image.getTitle());

            Instant uploadDate = image.getImgUploadDate();
            if (uploadDate != null) {
                String formattedDate = DateTimeFormatter.ISO_INSTANT.format(uploadDate);
                node.put("imgUploadDate", formattedDate);
            } else {
                node.putNull("imgUploadDate");
            }

            nodes.add(node);
        }

        return nodes;
    }

    @RequestMapping(value = "/images/{id}/similar", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public ResponseEntity<ArrayNode> getImageListSortedBySimilarity(
            @PathVariable("id") long id,
            @RequestParam(value = "descr") String descr,
            @RequestParam(value = "number", defaultValue = "5") int number) {

        // Validate the 'descr' parameter
        if (descr == null || descr.isEmpty() || 
            (!descr.equals("histogram_2d") && !descr.equals("histogram_3d") && !descr.equals("histogram_of_visual_words"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); 
        }
        
        List<ImageMetadata> images = imageRepository.getSortedImagesMetaIndexes(id, number, descr);

        // If no similar images found, return NOT_FOUND
        if (images.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); 
        }

        ArrayNode nodes = mapper.createArrayNode();
        
        // Loop through each image and add necessary details to the response
        for (ImageMetadata image : images) {
            ObjectNode node = mapper.createObjectNode();
            node.put("id", image.getId());
            node.put("name", image.getName());
            node.put("type", image.getFormat());
            node.put("size", image.getWidth() + "x" + image.getHeight());
            node.put("score", image.getScore());
            node.put("imgClass", image.getImgClass());
            node.put("imgClassConfidence", image.getImgClassConfidence()); // Now float
            node.put("imgUrl", image.getImgUrl());
            node.put("author", image.getAuthor());
            node.put("likeCount", image.getLikeCount());
            node.put("downloadCount", image.getDownloadCount());
            node.put("title", image.getTitle());

            Instant uploadDate = image.getImgUploadDate();
            if (uploadDate != null) {
                String formattedDate = DateTimeFormatter.ISO_INSTANT.format(uploadDate);
                node.put("imgUploadDate", formattedDate);
            } else {
                node.putNull("imgUploadDate");
            }

            // Add the image node to the array
            nodes.add(node);
        }

        // Return the list of similar images with OK status
        return ResponseEntity.status(HttpStatus.OK).body(nodes); 
    }

    @RequestMapping(value = "/images/{id}/like", method = RequestMethod.PUT)
public ResponseEntity<?> incrementLikeCount(@PathVariable("id") long id) {
    try {
        ImageMetadata imageMetaData = imageRepository.getImageMetaData(id);

        if (imageMetaData != null) {
            // Increment like count in the database
            imageRepository.incrementLikeCount(id);

            return new ResponseEntity<>("Like count incremented successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Image not found", HttpStatus.NOT_FOUND);
        }
    } catch (DataAccessException e) {
        e.printStackTrace();
        return new ResponseEntity<>("Error accessing the database", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

@RequestMapping(value = "/images/{id}/increment-download", method = RequestMethod.PUT)
public ResponseEntity<?> incrementViewCount(@PathVariable("id") long id) {
    try {
        ImageMetadata imageMetaData = imageRepository.getImageMetaData(id);

        if (imageMetaData != null) {
            // Increment view count in the database
            imageRepository.incrementDownloadCount(id);

            return new ResponseEntity<>("View count incremented successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Image not found", HttpStatus.NOT_FOUND);
        }
    } catch (DataAccessException e) {
        e.printStackTrace();
        return new ResponseEntity<>("Error accessing the database", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

@RequestMapping(value = "/images/{id}/like", method = RequestMethod.DELETE)
public ResponseEntity<?> decrementLikeCount(@PathVariable("id") long id) {
    try {
        ImageMetadata imageMetaData = imageRepository.getImageMetaData(id);

        if (imageMetaData != null) {
            // Decrement like count in the database
            imageRepository.decrementLikeCount(id);

            return new ResponseEntity<>("Like count decremented successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Image not found", HttpStatus.NOT_FOUND);
        }
    } catch (DataAccessException e) {
        e.printStackTrace();
        return new ResponseEntity<>("Error accessing the database", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

@RequestMapping(value = "/images/{id}/title", method = RequestMethod.PUT)
public ResponseEntity<?> updateImageTitle(@PathVariable("id") long id, 
                                           @RequestParam("title") String newTitle) {
    try {

        ImageMetadata imageMetaData = imageRepository.getImageMetaData(id);

        // Check if the image exists
        if (imageMetaData != null) {

            imageRepository.updateImageTitle(id, newTitle);

            return new ResponseEntity<>("Title updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Image not found", HttpStatus.NOT_FOUND);
        }
    } catch (DataAccessException e) {
        e.printStackTrace();
        return new ResponseEntity<>("Error accessing the database", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

}
