package util.timer;

public class AutoCorrectingLapse {
    private double secondsToLapse;
    private double elapsedTime;
    private long lastTime;
    private long numberOfLapseToDo;

    public AutoCorrectingLapse(double secondsToLapse) {
        this.secondsToLapse = secondsToLapse;
        this.elapsedTime = 0;
        this.lastTime = System.currentTimeMillis();
        this.numberOfLapseToDo = 0;
    }

    public void update() {
        elapsedTime += (System.currentTimeMillis() - lastTime)/1000.0;
        lastTime = System.currentTimeMillis();
        numberOfLapseToDo += round((elapsedTime - (elapsedTime % secondsToLapse))/secondsToLapse);
        elapsedTime %= secondsToLapse;
    }

    public boolean isTimeElapsed() {
        return numberOfLapseToDo > 0;
    }
    public void lapse() {
        numberOfLapseToDo--;
        if(numberOfLapseToDo > 10) {
            numberOfLapseToDo = 10;
        }
    }

    private long round(double x) {
        return (long)(x+0.5);
    }
}
