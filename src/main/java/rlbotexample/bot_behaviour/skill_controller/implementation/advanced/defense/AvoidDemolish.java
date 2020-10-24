package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.defense;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.BotBehaviour;
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

public class AvoidDemolish extends SkillController {

    private BotBehaviour bot;
    private int indexOfPlayerToAvoid;
    private ExtendedCarData carToAvoid;
    private Vector3 carDestination;
    private JumpController jumpController;

    private DrivingSpeedController drivingSpeedController;
    private GroundOrientationController groundOrientationController;

    public AvoidDemolish(BotBehaviour bot) {
        this.bot = bot;
        this.indexOfPlayerToAvoid = -1;
        this.carToAvoid = null;
        this.carDestination = new Vector3();

        this.drivingSpeedController = new DrivingSpeedController(bot);
        this.groundOrientationController = new GroundOrientationController(bot);

        this.jumpController = new JumpController(bot);
    }

    public void setPlayerToAvoid(int playerIndex) {
        this.indexOfPlayerToAvoid = playerIndex;
    }

    @Override
    public void updateOutput(DataPacket input) {
        carToAvoid = input.allCars.get(indexOfPlayerToAvoid);
        carDestination = findDestinationOfCarToAvoidDemo(input);

        drivingSpeedController.setSpeed(2300);
        drivingSpeedController.updateOutput(input);
        bot.output().boost(!input.car.isSupersonic);

        groundOrientationController.setDestination(carDestination);
        groundOrientationController.updateOutput(input);
        if(input.car.velocity.dotProduct(input.car.orientation.noseVector) < 0) {
            bot.output().drift(false);
        }
    }

    private Vector3 findDestinationOfCarToAvoidDemo(DataPacket input) {
        double closestTime = findClosestIntersectionTimeBetween4DCurves(input);
        Vector3 futurePositionOfCar = input.ballPrediction.carsAtTime(closestTime).get(input.playerIndex).position;
        Vector3 futurePositionOfCarToAvoid = input.ballPrediction.carsAtTime(closestTime).get(indexOfPlayerToAvoid).position;

        Vector3 deltaPosition = futurePositionOfCar.minus(futurePositionOfCarToAvoid);

        return futurePositionOfCar.plus(deltaPosition.scaled(20));
    }

    private double findClosestIntersectionTimeBetween4DCurves(DataPacket input) {
        double closestDistance = Double.MAX_VALUE;
        double timeOfProbableCollision = 0;
        double closestTimeOfProbableCollision = 0;

        for(int i = 0; i < input.ballPrediction.balls.size(); i++) {
            timeOfProbableCollision = input.ballPrediction.balls.get(i).time;
            Vector3 testSelf = input.ballPrediction.cars.get(i).get(input.playerIndex).position;
            Vector3 testCarToAvoid = input.ballPrediction.cars.get(i).get(indexOfPlayerToAvoid).position;
            if(testSelf.minus(testCarToAvoid).magnitude() < closestDistance) {
                closestDistance = testSelf.minus(testCarToAvoid).magnitude();
                closestTimeOfProbableCollision = timeOfProbableCollision;
            }
        }

        return closestTimeOfProbableCollision;
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        renderer.drawLine3d(Color.red, input.car.position, carDestination);
    }
}
