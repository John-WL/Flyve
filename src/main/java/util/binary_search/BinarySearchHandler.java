package util.binary_search;

import util.parameter_configuration.data.handler.DataHandler;
import util.parameter_configuration.data.representation.DataRepresentation;

import java.util.ArrayList;
import java.util.List;

public class BinarySearchHandler {

    private static final int DEFAULT_NUMBER_OF_FULL_SEARCH_TO_DO = 100;

    private List<BinarySearcher> searchers;
    private int indexOfActiveBinarySearcher;
    private int numberOfFullSearchDone;

    private BinarySearcher activeBinarySearcher;

    public BinarySearchHandler(DataRepresentation dataRepresentation) {
        // build all the binary searchers from specified file hierarchy (parameter file hierarchy = dataFileRepresentation)
        // in which the parameters are situated.
        searchers = new ArrayList<>();
        List<DataHandler> parametersToOptimize = dataRepresentation.getDataHandlerList();
        for(DataHandler parameter: parametersToOptimize) {
            searchers.add(new BinarySearcher(parameter));
        }

        numberOfFullSearchDone = 0;
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
                if(indexOfActiveBinarySearcher == searchers.size()) {
                    // if we reached the last one, reset its index to 0,
                    // and count the number of full searched that we did up to now.
                    indexOfActiveBinarySearcher = 0;
                    numberOfFullSearchDone++;
                }
                activeBinarySearcher = searchers.get(indexOfActiveBinarySearcher);

                // restart the search
                activeBinarySearcher.resetSearchRange();
                System.out.println("Starting a new search!");
            }
            // else, do nothing! There is nothing to search anymore!
        }
        // if the desired precision has not been attained yet,
        // update the current data with the results we got from the search
        else {
            activeBinarySearcher.nextHypothesis();
            System.out.println("Narrowing down a parameter!");
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

        // if we still need to refresh all the searchers for another search run
        if(numberOfFullSearchDone < DEFAULT_NUMBER_OF_FULL_SEARCH_TO_DO) {
            isDoneSearching = false;
        }

        for(BinarySearcher binarySearcher: searchers) {
            if(!binarySearcher.isDoneSearching()) {
                isDoneSearching = false;
            }
        }

        return isDoneSearching;
    }
}
