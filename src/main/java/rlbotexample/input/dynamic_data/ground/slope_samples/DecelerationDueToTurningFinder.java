package rlbotexample.input.dynamic_data.ground.slope_samples;

import util.math.linear_transform.LinearApproximator;
import util.math.vector.Vector2;

public class DecelerationDueToTurningFinder {

    private static LinearApproximator aInverse;

    static {
        aInverse = new LinearApproximator();

        aInverse.sample(new Vector2(-432.13079035626834, 2236.363424272832));
        aInverse.sample(new Vector2(-425.7666070079995, 2156.846194483909));
        aInverse.sample(new Vector2(-415.05901759516746, 2078.1071902603617));
        aInverse.sample(new Vector2(-402.63488015846633, 2000.3675301964288));
        aInverse.sample(new Vector2(-387.73142156625, 1923.8111990070909));
        aInverse.sample(new Vector2(-370.41075868423377, 1848.6095296645972));
        aInverse.sample(new Vector2(-351.71480011606036, 1774.9072317086868));
        aInverse.sample(new Vector2(-335.5965010621276, 1702.7366823275077));
        aInverse.sample(new Vector2(-326.0611240016351, 1631.5361584363447));
        aInverse.sample(new Vector2(-318.8772831933966, 1560.9836283014663));
        aInverse.sample(new Vector2(-311.59826106780656, 1491.020029592717));
        aInverse.sample(new Vector2(-313.0741290145352, 1421.2072761837462));
        aInverse.sample(new Vector2(-317.53960671039295, 1351.1535376093764));
        aInverse.sample(new Vector2(-320.6680836925534, 1280.7849394635275));
        aInverse.sample(new Vector2(-318.71093425528215, 1210.3740531590315));
        aInverse.sample(new Vector2(-310.6381826991219, 1140.4063242377147));
        aInverse.sample(new Vector2(-297.3450692060942, 1071.3663471174318));
        aInverse.sample(new Vector2(-278.8767011960135, 1003.7377333815409));
        aInverse.sample(new Vector2(-268.4028099535726, 937.3123349091497));
        aInverse.sample(new Vector2(-256.9420060127618, 871.7609008635313));
        aInverse.sample(new Vector2(-242.18842537997034, 807.3108041174687));
        aInverse.sample(new Vector2(-223.8350986221576, 744.2600883135395));
        aInverse.sample(new Vector2(-202.46955021775193, 682.9022446388982));
        aInverse.sample(new Vector2(-177.3264159835253, 623.5270575183818));
        aInverse.sample(new Vector2(-151.66257195785875, 566.2786071289737));
        aInverse.sample(new Vector2(-126.18547735339803, 511.168361620245));
        aInverse.sample(new Vector2(-108.26970622123895, 457.883213541434));
        aInverse.sample(new Vector2(-89.59716002672621, 406.0298175249391));
        aInverse.sample(new Vector2(-71.78573039706407, 355.69787105477303));
        aInverse.sample(new Vector2(-54.780846642861434, 306.81445791517484));
        aInverse.sample(new Vector2(-39.77314374300704, 259.2733669295878));
        aInverse.sample(new Vector2(-26.729311135957232, 212.8860533666768));
        aInverse.sample(new Vector2(-15.498809336994555, 167.46453583095078));
        aInverse.sample(new Vector2(-6.615763146162408, 122.89692562844226));
        aInverse.sample(new Vector2(0, 0));
        /*
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
        a.sample(new Vector2(0, 0));*/
    }

    public static double compute(double v, double w) {
        final double wMax = v/(2*Math.PI*TurningRadiusOfCar2.apply(v));
        return aInverse.inverse(v) * (w/wMax);
    }
}
