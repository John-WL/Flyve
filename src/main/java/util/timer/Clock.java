package util.timer;

public class Clock {

    private long timeOfStartNanos;
    private double deltaTimeSecs;
    private boolean hasStopped;

    public Clock() { hasStopped = false; }

    public void start()
    {
        timeOfStartNanos = System.nanoTime();
        hasStopped = false;
    }

    public void stop() {
        double lastNanoRead = System.nanoTime();
        deltaTimeSecs = (lastNanoRead - timeOfStartNanos)/1000000.0;
        hasStopped = true;
    }

    public double getElapsedSeconds() {
        if(!hasStopped) {
            return (System.nanoTime() - timeOfStartNanos)/1000000.0;
        }
        else {
            return deltaTimeSecs;
        }
    }

    private double timeSinceStartSecs() {
        return (System.nanoTime() - timeOfStartNanos)/1000000.0;
    }
}
