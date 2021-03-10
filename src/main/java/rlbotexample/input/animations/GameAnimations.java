package rlbotexample.input.animations;

import util.parameter_configuration.ObjectSerializer;

public class GameAnimations {

    private static final String BASE_FILE_PATH = "src\\main\\resources\\";

    public static final CarGroupAnimation boss_idle = ObjectSerializer.load(BASE_FILE_PATH + "boss rig\\boss_idle.sob");
    public static final CarGroupAnimation boss_attack = ObjectSerializer.load(BASE_FILE_PATH + "boss rig\\boss_attack.sob");
    public static final CarGroupAnimation boss_basic_rotation = ObjectSerializer.load(BASE_FILE_PATH + "boss rig\\boss_basic_rotation.sob");

}
