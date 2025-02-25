package pdl.backend;

import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.Resource;
import java.nio.file.Files;
import java.util.ArrayList;
import boofcv.struct.image.GrayU8;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.util.Iterator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.dao.DataAccessException;

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
import boofcv.struct.image.Planar;
import pdl.backend.ImageRepository;
import pdl.backend.ImageUtils;
import pdl.backend.ImageIndex;
import pdl.backend.ImageMetadata;

@RestController
public class ImageController {

    @Autowired
    private ObjectMapper mapper;
    private ImageRepository imageRepository;

    @Autowired
    public ImageController(ImageRepository imageRepository) {

        this.imageRepository = imageRepository;

        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:images/*");

            List<ImageIndex> imageIndexesToUpload = new ArrayList<>();

            for (Resource resource : resources) {

                String filename = resource.getFilename().toLowerCase();
                
                if (!filename.endsWith(".jpg") && !filename.endsWith(".png") && !filename.endsWith(".jpeg")) {
                    throw new IOException("Invalid image format: " + filename);
                }

                byte[] fileContent = Files.readAllBytes(resource.getFile().toPath());

                Planar<GrayU8> image = ImageUtils.loadImage(fileContent);
                BufferedImage bufferedImage = ImageIO.read(resource.getFile());
                int width = bufferedImage.getWidth();
                int height = bufferedImage.getHeight();
                
                ImageInputStream iis = ImageIO.createImageInputStream(resource.getFile());

                Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);
                String resourceFormat="";

                while (imageReaders.hasNext()) {
                 ImageReader reader = (ImageReader) imageReaders.next();
                 resourceFormat = reader.getFormatName();
                }

                int[][] histogram2D = ImageUtils.histogramHueSaturation(image);
                int[][][] histogram3D = ImageUtils.histogramOfRGB(image);

                ImageIndex imageIndex = new ImageIndex(filename, width, height, resourceFormat, histogram2D, histogram3D);
                imageIndexesToUpload.add(imageIndex);
            }

            // Upload the initial image indexes to the DB
            imageRepository.setInitialImageIndexes(imageIndexesToUpload);

        } catch (IOException e) {
            e.printStackTrace();
        }
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
            node.put("width", imageMetaData.getWidth());
            node.put("height", imageMetaData.getHeight());

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
        
        try{
        ImageMetadata imageMetaData = imageRepository.getImageMetaData(id);

        if (imageMetaData != null) {
            imageRepository.deleteImageIndex(id);
            return new ResponseEntity<>("Image deleted successfully from DB", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Image not found", HttpStatus.NOT_FOUND);
        }
        } 
        catch (DataAccessException e) {
        e.printStackTrace();
        return new ResponseEntity<>("Error accessing the database", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    }

@RequestMapping(value = "/images", method = RequestMethod.POST)
public ResponseEntity<?> addImage(@RequestParam("file") MultipartFile file,
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
        int[][] histogram2D = ImageUtils.histogramHueSaturation(image);
        int[][][] histogram3D = ImageUtils.histogramOfRGB(image);

        ImageIndex imageIndex = new ImageIndex(filename, image.getWidth(), image.getHeight(), contentType, histogram2D, histogram3D);

        imageRepository.addImageMetaData(imageIndex);

    } catch (IOException e) {
        e.printStackTrace();
        return new ResponseEntity<>("Failed to save image", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<>("Image added successfully", HttpStatus.CREATED);
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
            nodes.add(node);
        }

        return nodes;

}

}
