package pdl.backend;

import boofcv.alg.bow.ClusterVisualWords;
import boofcv.struct.feature.TupleDesc_F64;
import boofcv.struct.image.GrayU8;
import boofcv.abst.feature.dense.DescribeImageDense;
import boofcv.factory.feature.dense.ConfigDenseSurfStable;
import boofcv.factory.feature.dense.FactoryDescribeImageDense;
import boofcv.struct.image.Planar;

import ai.djl.training.dataset.Batch;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.NDArray;
import ai.djl.basicdataset.cv.classification.Cifar10;

import org.ddogleg.clustering.kmeans.StandardKMeans;
import org.ddogleg.clustering.kmeans.StandardKMeans_MT;
import org.ddogleg.clustering.misc.MeanArrayF64;
import org.ddogleg.clustering.kmeans.InitializePlusPlus;
import org.ddogleg.clustering.kmeans.InitializeKMeans;
import org.ddogleg.clustering.PointDistance;
import org.ddogleg.struct.DogArray;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class SimilarityComputing {

    public static int numClusters = 20;
    public static int sizeOfBatch = 32;

    public static void main(String[] args) throws IOException, TranslateException {
        // Step 1: Load images
        List<Planar<GrayU8>> cifar10Images = loadImagesFromCifar10();

        // Step 2: Extract descriptors
        List<TupleDesc_F64> descriptors = extractDescriptorsFromImages(cifar10Images);

        // Step 3: Convert descriptors to double[][]
        double[][] vectors = convertDescriptorsToArray(descriptors);

        List<double[]> vectorList = new ArrayList<>();
        for (double[] vector : vectors) vectorList.add(vector);

        // Step 4: Configure K-Means clustering
        PointDistance<double[]> Disto = new PointDistance<double[]>() {
            @Override
            public double distance(double[] a, double[] b) {
                double sum = 0;
                for (int i = 0; i < a.length; i++) {
                    double diff = a[i] - b[i];
                    sum += diff * diff;
                }
                return Math.sqrt(sum);
            }

            @Override
            public PointDistance<double[]> newInstanceThread() {
                return this;
            }
        };
        
        InitializeKMeans<double[]> myKMeansInitializer= new InitializePlusPlus<>();

        StandardKMeans<double[]> kmeans = new StandardKMeans_MT<>(
            new MeanArrayF64(64), 
            myKMeansInitializer,
            Disto,
            () -> new double[64]
        );

        // Step 5: Configure Bag of Visual Words
        ClusterVisualWords bow = new ClusterVisualWords(kmeans ,12345L);
        for (TupleDesc_F64 descr : descriptors) bow.addReference(descr);;

        bow.process(numClusters);

        // Step 7: Save the dictionary
        saveDictionary(kmeans, "l1b/backend/src/main/resources/new_visual_dictionary.dat");

        System.out.println("Bag of Visual Words dictionary created and saved successfully!");
    }

    private static List<GrayU8> loadImagesFromResources() {
        try {
            List<GrayU8> images = new ArrayList<>();
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:images/*");

            for (Resource resource : resources) {
                String filename = resource.getFilename().toLowerCase();
                if (!filename.endsWith(".jpg") && !filename.endsWith(".png") && !filename.endsWith(".jpeg")) {
                    throw new IOException("Invalid image format: " + filename);
                }
                byte[] fileContent = Files.readAllBytes(resource.getFile().toPath());
                Planar<GrayU8> image = ImageUtils.loadImage(fileContent);
                images.add(image.getBand(0));
            }

            return images;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Planar<GrayU8>> loadImagesFromCifar10() throws IOException, TranslateException {
        List<Planar<GrayU8>> images = new ArrayList<>();
        int batchCount = 0; // Counter to track the number of batches processed
    
        try (NDManager manager = NDManager.newBaseManager()) {
            Cifar10 cifar10 = Cifar10.builder()
                    .setSampling(sizeOfBatch, true)
                    .build();
    
            cifar10.prepare(new ProgressBar());
    
            // Load up to the first 20 batches
            for (Batch batch : cifar10.getData(manager)) {
                try {
                    NDArray batchData = batch.getData().head();
    
                    for (long i = 0; i < batchData.size(0); i++) {
                        NDArray singleImage = batchData.get(i);
                        images.add(convertToPlanar(singleImage)); // Convert to Planar<GrayU8>
                    }
                } finally {
                    batch.close();
                }
    
                batchCount++;
                if (batchCount >= 200) {
                    break; 
                }
            }
        }
    
        System.out.println("Successfully loaded " + images.size() + " images from the first 500 batches");
        return images;
    }
    
    private static Planar<GrayU8> convertToPlanar(NDArray image) {
        int channels = (int) image.getShape().get(0);  // Should be 3 for RGB
        int height = (int) image.getShape().get(1);    // Height of the image
        int width = (int) image.getShape().get(2);     // Width of the image
    
        if (channels != 3) {
            throw new IllegalArgumentException("Image does not have 3 channels (RGB). Found: " + channels);
        }
    
        // Create a Planar image with 3 bands (one for each channel)
        Planar<GrayU8> planarImage = new Planar<>(GrayU8.class, width, height, 3);
    
        // Loop over each pixel in the image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Extract the RGB channels from the NDArray
                float r = image.getFloat(0, y, x); // Red channel (0-1 range)
                float g = image.getFloat(1, y, x); // Green channel (0-1 range)
                float b = image.getFloat(2, y, x); // Blue channel (0-1 range)
    
                // Convert RGB to 8-bit grayscale values (0-255)
                int redValue = Math.max(0, Math.min(255, (int) (r * 255)));
                int greenValue = Math.max(0, Math.min(255, (int) (g * 255)));
                int blueValue = Math.max(0, Math.min(255, (int) (b * 255)));
    
                // Set the values in the corresponding bands of the Planar image
                planarImage.getBand(0).set(x, y, redValue);   // Red channel
                planarImage.getBand(1).set(x, y, greenValue); // Green channel
                planarImage.getBand(2).set(x, y, blueValue);  // Blue channel
            }
        }
    
        return planarImage;
    }
    
    
    private static List<TupleDesc_F64> extractDescriptorsFromImages(List<Planar<GrayU8>> images) {
        List<TupleDesc_F64> descriptors = new ArrayList<>();
        
        // Configure Dense SURF with finer sampling
        ConfigDenseSurfStable config = new ConfigDenseSurfStable();
        config.descriptorScale = 0.5; // Adjust based on desired descriptor size
        config.sampling.periodX = 2;  // Reduce to increase keypoint density
        config.sampling.periodY = 2;  // Reduce to increase keypoint density
        config.surf.widthLargeGrid = 4;
        
        // Extract descriptors for each image
        for (Planar<GrayU8> image : images) {
            // Process each band of the Planar image
            for (int band = 0; band < image.getNumBands(); band++) {
                // Extract the band (single channel image)
                GrayU8 bandImage = image.getBand(band);
                
                // Use DescribeImageDense for single-band images (GrayU8)
                DescribeImageDense<GrayU8, TupleDesc_F64> denseExtractor = 
                    FactoryDescribeImageDense.surfStable(config, GrayU8.class);
                
                denseExtractor.process(bandImage);
                List<TupleDesc_F64> imageDescriptors = denseExtractor.getDescriptions();
                descriptors.addAll(imageDescriptors);
            }
        }
        
        return descriptors;
    }
    

    private static double[][] convertDescriptorsToArray(List<TupleDesc_F64> descriptors) {
        double[][] array = new double[descriptors.size()][];
        for (int i = 0; i < descriptors.size(); i++) {
            TupleDesc_F64 descriptor = descriptors.get(i);
            array[i] = new double[descriptor.size()];
            for (int j = 0; j < descriptor.size(); j++) {
                array[i][j] = descriptor.getDouble(j);
            }
        }
        return array;
    }

   public static List<double[]> convertDogArrayToList(DogArray<double[]> dogArray) {
    List<double[]> list = new ArrayList<>();
    for (int i = 0; i < dogArray.size(); i++) {
        list.add(dogArray.get(i).clone()); // Clone the array to avoid reference issues
    }
    return list;
   }

   public static void saveDictionary(StandardKMeans<double[]> kmeans, String filePath) {
     List<double[]> clusters = convertDogArrayToList(kmeans.getBestClusters()); // Get the cluster centers
      try (FileWriter writer = new FileWriter(filePath)) {
        for (double[] cluster : clusters) {
            for (double value : cluster) {
                writer.write(value + ","); 
            }
            writer.write("\n"); 
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
  }

    public static List<double[]> loadClustersFromFile(String filePath) {
        List<double[]> clusters = new ArrayList<>();

        Resource resource = new ClassPathResource(filePath);

        try (BufferedReader reader = new BufferedReader(new FileReader(resource.getFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line by commas to get individual cluster values
                String[] values = line.split(",");
                double[] cluster = new double[values.length];

                // Parse each value as a double
                for (int i = 0; i < values.length; i++) {
                    cluster[i] = Double.parseDouble(values[i]);
                }

                // Add the cluster to the list
                clusters.add(cluster);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return clusters;
    }

    public static int[] computeHistogram(List<TupleDesc_F64> descriptors) {
        List<double[]> clusters = loadClustersFromFile("new_visual_dictionary.dat");
        int[] histogram = new int[clusters.size()];

        for (TupleDesc_F64 descriptor : descriptors) {
            int closestCluster = findClosestCluster(descriptor, clusters);
            histogram[closestCluster]++;
        }

        return histogram;
    }

    public static int findClosestCluster(TupleDesc_F64 feature, List<double[]> clusters) {
        int bestIndex = -1;
        double bestDistance = Double.MAX_VALUE;
    
        for (int i = 0; i < clusters.size(); i++) {
            double distance = 0;
            
            for (int j = 0; j < feature.size(); j++) {
                distance += Math.pow(feature.get(j) - clusters.get(i)[j], 2);
            }
    
            if (distance < bestDistance) {
                bestDistance = distance;
                bestIndex = i;
            }
        }
    
        return bestIndex;
    }
    
}