package util.parameter_configuration.binary_search;

public class BinarySearchHandler {

    public void nextHypothesis() {
        // change the BinarySearcher if the desired search precision
        // has been attained


        // create new file from the current best one and change
        // the values ever so slightly according to the binary searcher

    }

    public double sendSearchResult(double resultingEvaluation) {
        // carefully save the result, and compare it to the best one
        // that we had yet.
        // if the result is now the best one that we never had before,
        // then chose the current parameter as the new middle point
        // for the current active BinarySearcher.

    }

    public boolean isDoneSearching() {
        // returns true if we searched enough.
        // we searched enough if we reached the desired precision
        // for every BinarySearcher N times, where N is the number of times
        // that we want to do a binary search for every parameter.

    }

    public void isolateBestResultsInAFolder() {
        // helps to isolate the best parameters we got yet
        // when the data folder is going to be full of files

    }
}
