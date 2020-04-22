package util.parameter_configuration;

import static util.parameter_configuration.FileReader.LOCAL_CLASS_PATH;

public class ArbitraryValueSerializer {

    public static final String ARBITRARY_CFG_PATH = LOCAL_CLASS_PATH + "arbitrary_cfg\\";
    public static final String BOOST_FOR_THROTTLE_THRESHOLD_FILENAME = ARBITRARY_CFG_PATH + "boost_throttle_threshold_val.arb";
    public static final String BOOST_FOR_THROTTLE_DRIBBLE_THRESHOLD_FILENAME = ARBITRARY_CFG_PATH + "boost_throttle_dribble_threshold_val.arb";
    public static final String DRIFT_FOR_STEERING_THRESHOLD_FILENAME = ARBITRARY_CFG_PATH + "drift_steering_threshold_val.arb";

    public static double serialize(String fileName) {
        return Double.valueOf(FileReader.fileContent(fileName).get(0));
    }
}
