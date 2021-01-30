package rlbotexample.bot_behaviour.flyve.implementation.ml;

import com.github.jelmerk.knn.DistanceFunctions;
import com.github.jelmerk.knn.Item;
import com.github.jelmerk.knn.SearchResult;
import com.github.jelmerk.knn.hnsw.HnswIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.hash;
import static jdk.nashorn.internal.runtime.regexp.joni.Syntax.Java;

public class GeneralLinearApproximator {

    private final Map<Input, Output> function = new HashMap<>();
    private HnswIndex<float[], float[], Input, Float> hnswIndex;

    public GeneralLinearApproximator() {
        hnswIndex = HnswIndex
                .newBuilder(41, DistanceFunctions.FLOAT_EUCLIDEAN_DISTANCE, 10000)
                .withM(16)
                .withEf(200)
                .withEfConstruction(200)
                .build();
    }

    public void addSamplePoint(Input input, Output output) {
        function.put(input, output);
        hnswIndex.add(input);
    }


    public Output process(Input input) {
        int numberOfPointsToFind = input.values.length + 1;
        List<SearchResult<Input, Float>> approximateResults = hnswIndex.asExactIndex().findNeighbors(input.values, numberOfPointsToFind);

        List<Output> results = new ArrayList<>();
        for(SearchResult<Input, Float> result: approximateResults) {
            results.add(function.get(result.item()));
        }

        // AHHHH just for testing don't mind me...
        if(results.size() == 0) {
            return new Output(0, 0, 0, 0, 0, 0);
        }
        return results.get(0);
    }

    public int getSizeOfDataset() {
        return function.size();
    }


    public static class Input implements Item<float[], float[]> {
        public final float[] values;

        public Input(float... values) {
            this.values = values;
        }

        @Override
        public float[] id() {
            return values;
        }

        @Override
        public float[] vector() {
            return values;
        }

        @Override
        public int dimensions() {
            return values.length;
        }
    }

    public static class Output {
        public final double[] values;

        public Output(double... values) {
            this.values = values;
        }

        public double get(int index) {
            return values[index];
        }
    }
}
