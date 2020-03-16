package util.pid_controller;

public class PidController {

    private double kp;
    private double ki;
    private double kd;
    private double currentError;
    private double previousError;
    private double largeTotalError;
    private double smallTotalError;

    public PidController(double kp, double ki, double kd) {
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
        this.largeTotalError = 0;
        this.smallTotalError = 0;
    }

    public double process(double actualValue, double desiredValue) {
        // getting the error
        double error = actualValue - desiredValue;
        // updating the integral part
        // there are 2 total error variables because adding doubles
        // on a too large scale of magnitude difference does nothing at all
        smallTotalError += error;
        if(Math.abs(smallTotalError) > 10) {
            largeTotalError += smallTotalError;
            smallTotalError = 0;
        }
        // updating the derivative part
        previousError = currentError;
        currentError = error;

        return kp*error + ki*(largeTotalError + smallTotalError) - kd*(previousError - currentError);
    }
}
