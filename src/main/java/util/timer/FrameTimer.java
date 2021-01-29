package util.timer;

public class FrameTimer {

    public int numberOfFramesToCount;
    private int frameCounter;

    public FrameTimer(int numberOfFramesToCount) {
        this.numberOfFramesToCount = numberOfFramesToCount;
        this.frameCounter = 0;
    }

    public void start() {
        frameCounter = 0;
    }

    public boolean isTimeElapsed() {
        return frameCounter >= numberOfFramesToCount;
    }

    public void countFrame() {
        frameCounter++;
    }

}
