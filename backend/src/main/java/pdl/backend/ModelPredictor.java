package pdl.backend;

import org.tensorflow.SavedModelBundle;
import org.tensorflow.Tensor;
import org.tensorflow.types.TFloat32;
import org.tensorflow.ndarray.Shape;
import org.tensorflow.ndarray.FloatNdArray;
import org.tensorflow.ndarray.NdArrays;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ModelPredictor {
    
    private SavedModelBundle model;
    private List<String> classLabels;
    private static final String modelPath = ModelPredictor.class.getClassLoader()
            .getResource("new_cifar10_model").getPath();
    private static final String labelsPath = ModelPredictor.class.getClassLoader()
            .getResource("model_labels.txt").getPath();

    public ModelPredictor() throws IOException {
        model = SavedModelBundle.load(modelPath, "serve");
        classLabels = Files.readAllLines(Path.of(labelsPath));
    }

    public String[] classifyImage(String imagePath) throws IOException {
        BufferedImage img = ImageIO.read(new File(imagePath));
        Tensor preprocessedImage = preprocessImage(img);
        
        try (Tensor output = runInference(preprocessedImage)) {
            PredictionResult result = getMaxProbabilityIndex(output);
            return new String[]{classLabels.get(result.index), String.format("%.2f", result.confidence * 100)};
        }
    }

    public String[] classifyImage(byte[] imageData) throws IOException {
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageData));
        Tensor preprocessedImage = preprocessImage(img);
        
        try (Tensor output = runInference(preprocessedImage)) {
            PredictionResult result = getMaxProbabilityIndex(output);
            return new String[]{classLabels.get(result.index), String.format("%.2f", result.confidence * 100)};
        }
    }

    private Tensor preprocessImage(BufferedImage img) {
        BufferedImage resizedImage = resizeImage(img, 32, 32);
        float[][][][] imageArray = new float[1][32][32][3];
        
        for (int y = 0; y < 32; y++) {
            for (int x = 0; x < 32; x++) {
                int rgb = resizedImage.getRGB(x, y);
                imageArray[0][y][x][0] = ((rgb >> 16) & 0xFF) / 255.0f;
                imageArray[0][y][x][1] = ((rgb >> 8) & 0xFF) / 255.0f;
                imageArray[0][y][x][2] = (rgb & 0xFF) / 255.0f;
            }
        }
        
        FloatNdArray floatNdArray = NdArrays.ofFloats(Shape.of(1, 32, 32, 3));
        for (int y = 0; y < 32; y++) {
            for (int x = 0; x < 32; x++) {
                for (int c = 0; c < 3; c++) {
                    floatNdArray.setFloat(imageArray[0][y][x][c], 0, y, x, c);
                }
            }
        }
        return TFloat32.tensorOf(floatNdArray);
    }

    private Tensor runInference(Tensor input) {
        return model.session()
                .runner()
                .feed("serving_default_inputs", input)
                .fetch("StatefulPartitionedCall")
                .run()
                .get(0);
    }

    private PredictionResult getMaxProbabilityIndex(Tensor output) {
        FloatNdArray probabilitiesNdArray = (TFloat32) output;
        float[] probabilities = new float[(int) probabilitiesNdArray.shape().size(1)];
        
        for (int i = 0; i < probabilities.length; i++) {
            probabilities[i] = probabilitiesNdArray.getFloat(0, i);
        }
        
        int maxIndex = 0;
        float maxProb = probabilities[0];
        for (int i = 1; i < probabilities.length; i++) {
            if (probabilities[i] > maxProb) {
                maxProb = probabilities[i];
                maxIndex = i;
            }
        }
        return new PredictionResult(maxIndex, maxProb);
    }

    private static class PredictionResult {
        int index;
        float confidence;

        PredictionResult(int index, float confidence) {
            this.index = index;
            this.confidence = confidence;
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g.dispose();
        return resizedImage;
    }

    public static void main(String[] args) {
        try {
            String imagePath = "l1b/backend/src/main/resources/images/deer.jpg";

            if (!Files.exists(Path.of(modelPath))) {
                throw new IOException("Model directory not found: " + Path.of(modelPath).toAbsolutePath());
            }
            if (!Files.exists(Path.of(labelsPath))) {
                throw new IOException("Labels file not found: " + Path.of(labelsPath).toAbsolutePath());
            }
            if (!Files.exists(Path.of(imagePath))) {
                throw new IOException("Image file not found: " + Path.of(imagePath).toAbsolutePath());
            }
            
            ModelPredictor classifier = new ModelPredictor();
            String[] result = classifier.classifyImage(imagePath);
            System.out.println("Predicted Class: " + result[0] + ", Confidence: " + result[1] + "%");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
