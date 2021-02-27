package rlbotexample.input.dynamic_data.ground;

import util.math.linear_transform.LinearApproximator;
import util.math.vector.Vector2;

public class DecelerationDueToTurningFinder {

    private static LinearApproximator a;
    static {
        a = new LinearApproximator();

        a.sample(new Vector2(2300, -430));
        a.sample(new Vector2(2109.3583984375, -422.633056640625));
        a.sample(new Vector2(1975.194091796875, -402.492919921875));
        a.sample(new Vector2(1850.389404296875, -374.4140625));
        a.sample(new Vector2(1736.0645751953125, -342.9744873046875));
        a.sample(new Vector2(1628.087646484375, -323.9307861328125));
        a.sample(new Vector2(1522.28173828125, -317.417724609375));
        a.sample(new Vector2(1418.5565185546875, -311.1756591796875));
        a.sample(new Vector2(1361.6273193359375, -170.78759765625));
        a.sample(new Vector2(1324.82470703125, -110.4078369140625));
        a.sample(new Vector2(1298.517578125, -78.92138671875));
        a.sample(new Vector2(1279.968505859375, -55.647216796875));
        a.sample(new Vector2(1267.0948486328125, -38.6209716796875));
        a.sample(new Vector2(1258.1534423828125, -26.82421875));
        a.sample(new Vector2(1252.0572509765625, -18.28857421875));
        a.sample(new Vector2(1247.8050537109375, -12.756591796875));
        a.sample(new Vector2(1244.9681396484375, -8.5107421875));
        a.sample(new Vector2(1242.959716796875, -6.0252685546875));
        a.sample(new Vector2(1241.6663818359375, -3.8800048828125));
        a.sample(new Vector2(1240.7576904296875, -2.72607421875));
        a.sample(new Vector2(1240.15478515625, -1.8087158203125));
        a.sample(new Vector2(1239.7117919921875, -1.3289794921875));
        a.sample(new Vector2(1239.4578857421875, -0.76171875));
        a.sample(new Vector2(1239.2470703125, -0.6324462890625));
        a.sample(new Vector2(1239.1728515625, -0.22265625));
        a.sample(new Vector2(1239.0784912109375, -0.2830810546875));
        a.sample(new Vector2(1239.07275390625, -0.0172119140625));
        a.sample(new Vector2(1238.9908447265625, -0.2457275390625));
        a.sample(new Vector2(1238.97265625, -0.0545654296875));
        a.sample(new Vector2(1238.9251708984375, -0.1424560546875));
        a.sample(new Vector2(1238.91357421875, -0.0347900390625));
        a.sample(new Vector2(1238.910888671875, -0.008056640625));
        a.sample(new Vector2(1238.8, 0));
    }

    public static double compute(double v) {
        return a.compute(v);
    }
}
