package rlbotexample.bot_behaviour.basic_skills;

import rlbot.render.Renderer;
import rlbotexample.input.dynamic_data.DataPacket;
import util.timer.Timer;


public abstract class SkillController {

    private static final double TIME_BEFORE_RELOADING_PIDS = 1;

    private Timer pidParamReloadTime;

    SkillController() {
        pidParamReloadTime = new Timer(TIME_BEFORE_RELOADING_PIDS);
        pidParamReloadTime.start();
    }

    abstract void updateOutput(DataPacket input);
    abstract void updatePidValuesAndArbitraries();
    public abstract void debug(Renderer renderer, DataPacket input);

    public void setupAndUpdateOutputs(DataPacket input) {
        // setup PIDs and other useful variables
        if(pidParamReloadTime.isTimeElapsed()) {
            pidParamReloadTime.start();
            updatePidValuesAndArbitraries();
        }

        // update the output states for the bot depending on
        // which implementation is being used
        updateOutput(input);
    }
}
