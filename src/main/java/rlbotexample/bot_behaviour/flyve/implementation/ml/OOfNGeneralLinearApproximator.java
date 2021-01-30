package rlbotexample.bot_behaviour.flyve.implementation.ml;

import java.util.HashMap;
import java.util.Map;

public class OOfNGeneralLinearApproximator {

    private final Map<Input, Output> function = new HashMap<>();

    public OOfNGeneralLinearApproximator() {
    }

    public void addSamplePoint(Input input, Output output) {
        function.put(input, output);
    }


    public Output process(Input input) {
        //int numberOfPointsToFind = input.values.length + 1;
        Output result = findClosestPointOrElse(input, new Output(0, 0, 0, 0, 0, 0));

        return result;
    }

    private Output findClosestPointOrElse(Input input, Output defaultValue) {
        Output result = defaultValue;
        double bestDistanceYet = Double.MAX_VALUE;

        for(Input element: function.keySet()) {
            double testDistance = input.distanceSquared(input);
            if(bestDistanceYet > testDistance) {
                bestDistanceYet = testDistance;
                result = function.get(element);
            }
        }

        return result;
    }

    public int getSizeOfDataset() {
        return function.size();
    }


    public static class Input {
        public final float[] values;

        public Input(float... values) {
            this.values = values;
        }

        public double distanceSquared(Input input) {
            double distanceSquared = 0;

            for (float value : values) {
                distanceSquared += value * value;
            }

            return distanceSquared;
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
