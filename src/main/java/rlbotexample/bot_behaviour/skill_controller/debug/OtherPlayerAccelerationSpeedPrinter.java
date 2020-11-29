package rlbotexample.bot_behaviour.skill_controller.debug;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.input.dynamic_data.DataPacket;
import util.math.vector.Vector2;
import util.math.vector.Vector3;

public class OtherPlayerAccelerationSpeedPrinter extends SkillController {

    private double currentPlayerSpeed;
    private double lastPlayerSpeed;
    private double currentAcceleration;
    private int counter;

    public OtherPlayerAccelerationSpeedPrinter() {
        super();
        currentPlayerSpeed = 0;
        lastPlayerSpeed = 0;
        currentAcceleration = 0;
        counter = 0;
    }

    @Override
    public void updateOutput(DataPacket input) {
        // drive and turn to reach destination F
        lastPlayerSpeed = currentPlayerSpeed;
        currentPlayerSpeed = input.allCars.get(input.allCars.size()-1).velocity.dotProduct(input.allCars.get(input.allCars.size()-1).orientation.noseVector);

        currentAcceleration = currentAcceleration + (currentPlayerSpeed - (lastPlayerSpeed));

        counter++;
        if(counter == 1) {
            counter = 0;
            System.out.println("a.sample(new Vector2(" + currentPlayerSpeed + ", " + currentAcceleration*30 + "));");
            currentAcceleration = 0;
        }
    }

    @Override
    public void setupController() {
    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
    }
}
