package util.parameter_configuration;

import util.controllers.PidController;

import java.util.List;

import static util.parameter_configuration.FileReader.LOCAL_CLASS_PATH;

public class PidSerializer {
    public static final String PID_CFG_PATH = LOCAL_CLASS_PATH + "pid_cfg\\";
    public static final String THROTTLE_FILENAME = PID_CFG_PATH + "pid_throttle_val.pcg";
    public static final String STEERING_FILENAME = PID_CFG_PATH + "pid_steering_val.pcg";
    public static final String PITCH_YAW_ROLL_FILENAME = PID_CFG_PATH + "pid_pitch_yaw_roll_val.pcg";
    public static final String AERIAL_ANGLE_FILENAME = PID_CFG_PATH + "pid_aerial_angle_val.pcg";
    public static final String AERIAL_BOOST_FILENAME = PID_CFG_PATH + "pid_aerial_boost_val.pcg";
    public static final String DRIBBLE_FILENAME = PID_CFG_PATH + "pid_dribble_val.pcg";

    public static PidController serialize(String fileName, PidController previousPid) {
        List<String> parameters = FileReader.fileContent(fileName);

        double kp = Double.valueOf(parameters.get(0));
        double ki = Double.valueOf(parameters.get(1));
        double kd = Double.valueOf(parameters.get(2));

        PidController newPid = new PidController(kp, ki, kd);
        previousPid.transferInternalMemoryTo(newPid);

        return newPid;
    }
}
