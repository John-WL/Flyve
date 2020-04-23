package util.binary_search;

import util.parameter_configuration.data.handler.DataHandler;
import util.parameter_configuration.data.representation.DataRepresentation;

import java.util.ArrayList;
import java.util.List;

public class BinarySearchHandler {

    private List<BinarySearcher> searchers;
    private int indexOfActiveBinarySearcher;

    private BinarySearcher activeBinarySearcher;

    public BinarySearchHandler(DataRepresentation dataRepresentation) {
        // build all the binary searchers from specified file hierarchy (parameter file hierarchy = dataFileRepresentation)
        // in which the parameters are situated.
        searchers = new ArrayList<>();
        List<DataHandler> parametersToOptimize = dataRepresentation.getDataHandlerList();
        for(DataHandler parameter: parametersToOptimize) {
            searchers.add(new BinarySearcher(parameter));
        }

        indexOfActiveBinarySearcher = 0;
        activeBinarySearcher = searchers.get(indexOfActiveBinarySearcher);
    }

    public void nextHypothesis() {
        // change the BinarySearcher if the desired search precision
        // has been attained
        activeBinarySearcher = searchers.get(indexOfActiveBinarySearcher);
        if(activeBinarySearcher.isDoneSearching()) {
            if(!isDoneSearching()) {
                // change the active binary searcher
                indexOfActiveBinarySearcher++;
                indexOfActiveBinarySearcher %= searchers.size();
                activeBinarySearcher = searchers.get(indexOfActiveBinarySearcher);

                // restart the search
                activeBinarySearcher.resetSearchRange();
            }
            // else, do nothing! There is nothing to search anymore!
        }
        // if the desired precision has not been attained yet,
        // update the current data with the results we got from the search
        else {
            activeBinarySearcher.nextHypothesis();
        }
    }

    public void sendSearchResult(double resultingEvaluation) {
        // carefully save the result, and compare it to the best one
        // that we had yet.
        // if the result is now the best one that we never had before,
        // then chose the current parameters as the new middle point
        // for the current active BinarySearcher.

        searchers.get(indexOfActiveBinarySearcher).confrontHypothesis(resultingEvaluation);
    }

    public boolean isDoneSearching() {
        // returns true if we searched enough.
        // we searched enough if we reached the desired precision
        // for every BinarySearcher N times, where N is the number of times
        // that we want to do a binary search for every parameter.
        boolean isDoneSearching = true;

        for(BinarySearcher binarySearcher: searchers) {
            if(!binarySearcher.isDoneSearching()) {
                isDoneSearching = false;
            }
        }

        return isDoneSearching;
    }
}
