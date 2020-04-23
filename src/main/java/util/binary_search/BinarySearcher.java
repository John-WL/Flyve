package util.binary_search;

import util.parameter_configuration.data.handler.DataHandler;

public class BinarySearcher {

    private static final double DEFAULT_SEARCH_RANGE = 1;
    private static final double DEFAULT_PRECISION = 0.0001;

    private DataHandler dataHandler;
    private double searchRange;
    private double desiredPrecision;
    private double bestHypothesis;
    private double evaluationOfLeftSearch;
    private double evaluationOfRightSearch;
    private SearchState currentSearchPosition;

    public BinarySearcher(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
        this.bestHypothesis = DEFAULT_SEARCH_RANGE/2;
        this.searchRange = DEFAULT_SEARCH_RANGE;
        this.desiredPrecision = DEFAULT_PRECISION;
        this.evaluationOfLeftSearch = 1;
        this.evaluationOfRightSearch = 0;
        this.currentSearchPosition = SearchState.LEFT;
    }

    public void confrontHypothesis(double resultFromHypothesis) {
        // if we're doing our first search round (a search round is a topple: left and right both needs to
        // be searched before we can conclude anything)
        //
        if(currentSearchPosition == SearchState.LEFT) {
            evaluationOfLeftSearch = resultFromHypothesis;
        }
        else if(currentSearchPosition == SearchState.RIGHT) {
            evaluationOfRightSearch = resultFromHypothesis;
        }
    }

    public void nextHypothesis() {
        // if we were testing the left hypothesis,
        // then we're now going to test the right one. We'll
        // also update the best hypothesis, as we now have both
        // the right and the left test that have been done thoroughly.

        // if we were testing the right one, then we're now going to
        // test the right one.

        if(currentSearchPosition == SearchState.LEFT) {
            System.out.println("Left in the binary searcher!");
            // yes, this is very bad the first time we call this function.
            // Why? Because it directly starts by applying the hypothesis, before even evaluating it.

            // this is due to the fact that the code needs to guess the left value,
            // then do the training pack...
            // then do the right value,
            // then do the training pack again...
            // (yes this class tries to be general, but it has to work with
            // the current code it was originally designed for...)

            // and THEN, in the SAME nextHypothesis() call that searches back to the left hypothesis
            // (just before we update to the left), we need to update the current best value, so the
            // next left hypothesis (which is bestHypothesis + searchRange...) is a narrower one.

            // And so, it is bad at first, but it lets us do the rest of the execution very beautifully.
            // If anyone has a better idea than what I had, I'm very open to discussion :).
            searchRange /= 2;
            if(evaluationOfLeftSearch > evaluationOfRightSearch) {
                bestHypothesis -= searchRange;
            }
            else {
                bestHypothesis += searchRange;
            }
            // update
            double nextHypothesis = bestHypothesis + searchRange;
            dataHandler.set(nextHypothesis);
            currentSearchPosition = SearchState.RIGHT;
        }
        else {
            System.out.println("Right in the binary searcher!");
            double nextHypothesis = bestHypothesis - searchRange;
            dataHandler.set(nextHypothesis);
            currentSearchPosition = SearchState.LEFT;
        }
    }

    public boolean isDoneSearching() {
        return searchRange < desiredPrecision;
    }

    public void setStartingHypothesis(double startingValue) {
        // the search range here is going to get removed from
        // the best hypothesis when we'll start the search.
        // It's the best way I found right now to make this work.
        // don't worry too much about it.
        bestHypothesis = startingValue;
        bestHypothesis += searchRange/2;

        // this is to actually solve the problem we just created.
        // see "nextHypothesis()", in the first if, if you don't get what I mean.
        evaluationOfLeftSearch = 1;
        evaluationOfRightSearch = 0;

    }

    public void setSearchRange(double newSearchRange) {
        searchRange = newSearchRange;
    }

    public void resetSearchRange() {
        searchRange = DEFAULT_SEARCH_RANGE;
    }

    public void setPrecision(double newDesiredPrecision) {
        this.desiredPrecision = newDesiredPrecision;
    }

}
