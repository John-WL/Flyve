package util.math;

public class CalculusApproximation {

    public static final double[] GAUSSIAN_CONSTANT_XI = {-Math.sqrt(1/3.0), Math.sqrt(1/3.0)};
    public static final double[] GAUSSIAN_CONSTANT_WI = {1, 1};

    public static Function derivative(Function f, double h) {
        // doing an approximate limit:
        //
        // f(x+h) - f(x)
        // -------------
        //       h
        //
        // -> f = interpolate
        // -> t = x;
        // -> h = DERIVATIVE_PRECISION.

        return (double x) -> (
                (f.evaluate(x+h) - (f.evaluate(x))) / h
        );

    }

    public static double integral(Function f, double a, double b) {
        // doing a gaussian quadrature evaluation:
        //

        double c0 = ((b-a)/2);
        double c1 = ((b+a)/2);

        return c0 * (
                GAUSSIAN_CONSTANT_WI[0]*f.evaluate(c0*GAUSSIAN_CONSTANT_XI[0] + c1) +
                        GAUSSIAN_CONSTANT_WI[1]*f.evaluate(c0*GAUSSIAN_CONSTANT_XI[1] + c1)
        );

    }
}
