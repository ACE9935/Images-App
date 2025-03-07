package pdl.backend;

import boofcv.alg.bow.ClusterVisualWords;
import boofcv.struct.feature.TupleDesc_F64;
import boofcv.struct.image.GrayU8;
import boofcv.abst.feature.dense.DescribeImageDense;
import boofcv.factory.feature.dense.ConfigDenseSurfStable;
import boofcv.factory.feature.dense.FactoryDescribeImageDense;
import org.ddogleg.clustering.kmeans.StandardKMeans;
import org.ddogleg.clustering.kmeans.StandardKMeans_MT;
import org.ddogleg.clustering.misc.MeanArrayF64;
import org.ddogleg.clustering.kmeans.InitializePlusPlus;
import org.ddogleg.clustering.kmeans.InitializeKMeans;
import org.ddogleg.clustering.PointDistance;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.ddogleg.struct.DogArray;
import boofcv.struct.image.Planar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import pdl.backend.ImageUtils;

public class SimilarityComputing {

    public static int numClusters = 200;

    public static void main(String[] args) {
        // Step 1: Load images
        List<GrayU8> images = loadImagesFromResources();

        // Step 2: Extract descriptors
        List<TupleDesc_F64> descriptors = extractDescriptorsFromImages(images);

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
        saveDictionary(kmeans, "l1b/backend/src/main/resources/visual_dictionary.dat");

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

    private static List<TupleDesc_F64> extractDescriptorsFromImages(List<GrayU8> images) {
        List<TupleDesc_F64> descriptors = new ArrayList<>();
        DescribeImageDense<GrayU8, TupleDesc_F64> denseExtractor =
            FactoryDescribeImageDense.surfStable(new ConfigDenseSurfStable(), GrayU8.class);

        for (GrayU8 image : images) {
            denseExtractor.process(image);
            descriptors.addAll(denseExtractor.getDescriptions());
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
                writer.write(value + ","); // Write each value in the cluster
            }
            writer.write("\n"); // New line for the next cluster
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
        List<double[]> clusters = loadClustersFromFile("visual_dictionary.dat");
        int[] histogram = new int[clusters.size()];

        for (TupleDesc_F64 descriptor : descriptors) {
            int closestCluster = findClosestCluster(descriptor, clusters);
            histogram[closestCluster]++;
        }

        return histogram;
    }

    private static int findClosestCluster(TupleDesc_F64 feature, List<double[]> clusters) {
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