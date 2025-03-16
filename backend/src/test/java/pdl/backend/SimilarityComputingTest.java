package pdl.backend;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import boofcv.struct.feature.TupleDesc_F64;
import java.util.ArrayList;
import java.util.List;

public class SimilarityComputingTest {

    @Test
    public void testComputeHistogram() {
        List<TupleDesc_F64> descriptors = new ArrayList<>();
        descriptors.add(new TupleDesc_F64(64));
        descriptors.add(new TupleDesc_F64(64));

        int[] histogram = SimilarityComputing.computeHistogram(descriptors);
        assertNotNull(histogram);
        assertEquals(SimilarityComputing.numClusters, histogram.length);
    }

    @Test
public void testFindClosestCluster() {
    List<double[]> clusters = new ArrayList<>();
    clusters.add(new double[] { 1.0, 2.0, 3.0 });
    clusters.add(new double[] { 4.0, 5.0, 6.0 });

    TupleDesc_F64 feature = new TupleDesc_F64(3); 
    feature.data[0] = 1.0; 
    feature.data[1] = 2.0; 
    feature.data[2] = 3.0; 

    int closestCluster = SimilarityComputing.findClosestCluster(feature, clusters);
    assertEquals(0, closestCluster); 
}
}