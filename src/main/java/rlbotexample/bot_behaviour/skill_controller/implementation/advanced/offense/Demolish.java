package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundOrientationController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.JumpController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.ShortJump;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.SpeedFlip;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.Wait;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.car.ExtendedCarData;
import util.math.vector.Vector3;

import java.awt.*;

public class Demolish extends SkillController {

    private BotBehaviour bot;
    private int indexOfPlayerToDemolish;
    private ExtendedCarData carToDemo;
    private Vector3 carDestination;

    private DrivingSpeedController drivingSpeedController;
    private GroundOrientationController groundOrientationController;
    private JumpController jumpController;

    public Demolish(BotBehaviour bot) {
        this.bot = bot;
        this.indexOfPlayerToDemolish = -1;
        this.carToDemo = null;
        this.carDestination = new Vector3();

        this.drivingSpeedController = new DrivingSpeedController(bot);
        this.groundOrientationController = new GroundOrientationController(bot);
        this.jumpController = new JumpController(bot);
    }

    public void setPlayerToDemolish(int playerIndex) {
        this.indexOfPlayerToDemolish = playerIndex;
    }

    @Override
    public void updateOutput(DataPacket input) {
        carToDemo = input.allCars.get(indexOfPlayerToDemolish);
        carDestination = findDestinationOfCarToDemo(input);

        drivingSpeedController.setSpeed(2300);
        drivingSpeedController.updateOutput(input);

        groundOrientationController.setDestination(carDestination);
        groundOrientationController.updateOutput(input);
        if(input.car.velocity.dotProduct(input.car.orientation.noseVector) < 0) {
            bot.output().drift(false);
        }
        if(!bot.output().drift()) {
            bot.output().boost(!input.car.isSupersonic);
        }

        if(input.car.position.minus(carToDemo.position).magnitude() > 3000
                && input.car.position.minus(carDestination).magnitude() > 3000
                && input.car.velocity.dotProduct(input.car.orientation.noseVector) > 800
                && !input.car.isSupersonic
                && input.car.orientation.noseVector.dotProduct(carDestination.minus(input.car.position).normalized()) > 0.9) {
            jumpController.setFirstJumpType(new ShortJump(), input);
            jumpController.setSecondJumpType(new SpeedFlip(), input);
        }
        else {
            jumpController.setFirstJumpType(new Wait(), input);
            jumpController.setSecondJumpType(new Wait(), input);
        }
        jumpController.setJumpDestination(carDestination);
        //jumpController.updateOutput(input);
    }

    private Vector3 findDestinationOfCarToDemo(DataPacket input) {
        double distance = carToDemo.position.minus(input.car.position).magnitude();
        double speed = -carToDemo.velocity.minus(input.car.velocity).dotProduct(carToDemo.position.minus(input.car.position).normalized());
        double timeBeforeDemo = distance/speed;

        return input.statePrediction.carsAtTime(timeBeforeDemo).get(indexOfPlayerToDemolish).position;
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        renderer.drawLine3d(Color.red, input.car.position, carDestination);
    }
}
