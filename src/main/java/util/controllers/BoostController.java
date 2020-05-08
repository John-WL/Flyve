package util.controllers;

public class BoostController {

    private final static PwmController pwmController = new PwmController(8);

    public static double process(double accelerationRatio) {
        return booelanToDouble(pwmController.process(accelerationRatio));
    }

    private static double booelanToDouble(boolean valueToConvert) {
        if(valueToConvert) {
            return 1;
        }
        return 0;
    }
}
