package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.recovery;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation.AerialOrientationController2;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation.AerialOrientationController5;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.JumpController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.car.CarData;
import rlbotexample.input.dynamic_data.car.hit_box.HitBox;
import rlbotexample.input.geometry.StandardMapSplitMesh;
import rlbotexample.output.BotOutput;
import util.game_constants.RlConstants;
import util.math.vector.Vector;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;

import java.awt.*;

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
        Vector3 landingPosition = landingCar.position;
        HitBox collisionHitBox = input.car.hitBox.generateHypotheticalHitBox(landingPosition);
        Vector3 landingNormal = StandardMapSplitMesh.STANDARD_MAP_MESH
                .collideWith(collisionHitBox)
                .direction;
        // 10 seconds is long enough.
        // If "landingTime" is bigger than 10, this means that we
        // are not detecting any collisions with the map.
        Vector3 noseDirection = landingCar.velocity
                .minus(landingCar.velocity.projectOnto(landingNormal));
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