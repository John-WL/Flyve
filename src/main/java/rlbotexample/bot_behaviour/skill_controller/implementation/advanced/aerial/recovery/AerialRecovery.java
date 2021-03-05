package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerial.recovery;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation.AerialOrientationController5;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.car.CarData;
import util.math.vector.Vector3;

public class AerialRecovery extends SkillController {

    private BotBehaviour bot;
    private AerialOrientationController5 aerialController;

    public AerialRecovery(BotBehaviour bot) {
        this.bot = bot;
        this.aerialController = new AerialOrientationController5(bot);
    }

    @Override
    public void updateOutput(DataPacket input) {
        double landingTime = input.statePrediction.carBounceTimes(input.car);
        CarData landingCar = input.statePrediction.carsAtTime(landingTime).get(input.playerIndex);
        Vector3 landingNormal = input.statePrediction.findLandingNormal(input.car).direction;
        Vector3 noseDirection = landingCar.velocity
                .minus(landingCar.velocity.projectOnto(landingNormal));
        // it sometimes happen that the landing velocity is 0 if
        // we're already on the ground for example, so we're preventing the NaN
        // from propagating further away here
        if(!Double.isNaN(noseDirection.magnitude())) {
            aerialController.setNoseOrientation(noseDirection);
            aerialController.setRollOrientation(landingNormal);
        }
        aerialController.updateOutput(input);
    }

    @Override
    public void setupController() {
    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        aerialController.debug(renderer, input);
    }
}
