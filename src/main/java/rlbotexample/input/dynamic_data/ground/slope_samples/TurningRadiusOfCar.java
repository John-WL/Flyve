package rlbotexample.input.dynamic_data.ground.slope_samples;

import util.math.linear_transform.LinearApproximator;
import util.math.vector.Vector2;

public class TurningRadiusOfCar {

    private static LinearApproximator r;
    static {
        r = new LinearApproximator();

        r.sample(new Vector2(0, 137.72739871461724));
        r.sample(new Vector2(262.8271919199001, 174.9370699684086));
        r.sample(new Vector2(480.5803620363612, 226.7412641075541));
        r.sample(new Vector2(629.8568041626604, 267.88625286466277));
        r.sample(new Vector2(752.7938794915909, 304.0841950412389));
        r.sample(new Vector2(848.4169449627937, 339.52838631642635));
        r.sample(new Vector2(920.4800378063611, 372.4815117802509));
        r.sample(new Vector2(987.5395561697769, 409.6467905156257));
        r.sample(new Vector2(1032.4040512318809, 434.23750919555584));
        r.sample(new Vector2(1074.7298497762124, 451.71710438041856));
        r.sample(new Vector2(1108.0596779957295, 465.628037480382));
        r.sample(new Vector2(1130.983808460581, 475.70095384691626));
        r.sample(new Vector2(1151.5993226812875, 485.18834090484523));
        r.sample(new Vector2(1168.8906065154258, 493.3464404274104));
        r.sample(new Vector2(1182.1175914434232, 499.8573509226259));
        r.sample(new Vector2(1192.8527780074119, 505.27266365153145));
        r.sample(new Vector2(1201.5956058508204, 509.83985982269405));
        r.sample(new Vector2(1208.7124347833937, 513.5786483270073));
        r.sample(new Vector2(1214.263820180771, 516.5080104550692));
        r.sample(new Vector2(1244.132127227651, 530.3635677626522));
        r.sample(new Vector2(1382.9269232320266, 611.5330366858491));
        r.sample(new Vector2(1506.0924606411122, 716.2285360838107));
        r.sample(new Vector2(1624.738748230004, 798.8252771276838));
        r.sample(new Vector2(1737.031663499546, 874.3697654137572));
        r.sample(new Vector2(1842.5127543656245, 935.1859930442145));
        r.sample(new Vector2(1955.4328165396018, 982.7727873634734));
        r.sample(new Vector2(2053.509069860662, 1026.595441303153));
        r.sample(new Vector2(2148.154673202095, 1073.4815130836157));
        r.sample(new Vector2(2249.5878511407373, 1129.929492376098));
        r.sample(new Vector2(2300, 1165));
    }

    public static Double apply(Double v) {
        return TurningRadiusOfCar.r.compute(v);
    }

    public static Double inverse(Double r) {
        return TurningRadiusOfCar.r.inverse(r);
    }
}
