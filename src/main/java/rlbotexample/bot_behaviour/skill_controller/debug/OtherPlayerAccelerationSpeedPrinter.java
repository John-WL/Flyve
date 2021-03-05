package rlbotexample.bot_behaviour.skill_controller.debug;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.ground.slope_samples.MaxAccelerationFromThrottleFinder;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;

public class OtherPlayerAccelerationSpeedPrinter extends SkillController {

    private Vector3 currentPlayerSpeed;
    private Vector3 lastPlayerSpeed;
    private double currentAcceleration;
    private int counter;

    private double playerSpin;

    public OtherPlayerAccelerationSpeedPrinter() {
        super();
        currentPlayerSpeed = new Vector3();
        lastPlayerSpeed = new Vector3();
        currentAcceleration = 0;
        counter = 0;

        playerSpin = 0;
    }

    @Override
    public void updateOutput(DataPacket input) {
        lastPlayerSpeed = currentPlayerSpeed;
        currentPlayerSpeed = input.allCars.get(1-input.playerIndex).velocity;
        currentAcceleration = currentPlayerSpeed.magnitude() - lastPlayerSpeed.magnitude();

        input.allCars.get(1-input.playerIndex).orientation.noseVector
                .scaled(MaxAccelerationFromThrottleFinder.compute(currentPlayerSpeed.magnitude()));

        counter++;
        if(counter == 5) {
            counter = 0;
            System.out.println(currentAcceleration * RlConstants.BOT_REFRESH_RATE + ", " + currentPlayerSpeed.magnitude());
        }
    }

    @Override
    public void setupController() {
    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
    }
}
