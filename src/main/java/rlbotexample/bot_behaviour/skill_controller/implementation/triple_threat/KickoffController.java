package rlbotexample.bot_behaviour.skill_controller.implementation.triple_threat;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.input.dynamic_data.DataPacket;
import util.game_constants.RlConstants;

public class KickoffController extends SkillController {
    private BotBehaviour bot;
    private final DrivingSpeedController drivingSpeedController;

    public KickoffController(BotBehaviour bot) {
        super();
        this.bot = bot;
        drivingSpeedController = new DrivingSpeedController(bot);
        drivingSpeedController.setSpeed(RlConstants.CAR_MAX_SPEED);
    }

    @Override
    public void updateOutput(DataPacket input) {
        drivingSpeedController.updateOutput(input);
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
    }
}
