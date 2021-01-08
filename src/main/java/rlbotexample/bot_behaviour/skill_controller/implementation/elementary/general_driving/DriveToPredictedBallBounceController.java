package rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.Dribble2;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation.AerialOrientationHandler;
import rlbotexample.input.dynamic_data.ball.BallData;
import rlbotexample.input.dynamic_data.car.CarData;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector3;

import java.util.List;

public class DriveToPredictedBallBounceController extends SkillController {

    final private BotBehaviour bot;
    final private AerialOrientationHandler aerialOrientationHandler;
    double speedToReach;
    GroundOrientationController groundOrientationController;
    DrivingSpeedController drivingSpeedController;

    Dribble2 dribbleController;
    Vector3 ballDestination;

    public DriveToPredictedBallBounceController(BotBehaviour bot) {
        this.bot = bot;
        this.aerialOrientationHandler = new AerialOrientationHandler(bot);
        speedToReach = 2300;
        groundOrientationController = new GroundOrientationController(bot);
        drivingSpeedController = new DrivingSpeedController(bot);
        dribbleController = new Dribble2(bot);
        ballDestination = new Vector3();
    }

    public void setDestination(Vector3 ballDestination) {
        this.ballDestination = ballDestination;
    }

    @Override
    public void updateOutput(DataPacket input) {
        final BotOutput output = bot.output();
        final List<Double> timeOfBallBounces = input.statePrediction.ballBounceTimes();
        final BallData futureBall;

        double actualTimeOfBallBounce = Double.MAX_VALUE;
        for(Double timeOfBallBounce: timeOfBallBounces) {
            if(timeOfBallBounce < actualTimeOfBallBounce && input.statePrediction.ballAtTime(timeOfBallBounce).position.minus(input.car.position).magnitude()/timeOfBallBounce < 2300) {
                actualTimeOfBallBounce = timeOfBallBounce;
            }
        }

        futureBall = input.statePrediction.ballAtTime(actualTimeOfBallBounce);
        /*
        if(Math.abs(input.ball.velocity.z) > 80) {
            futureBall = input.ballPrediction.ballAtTime(actualTimeOfBallBounce);
        }
        else {
            //System.out.println("ground prediction");
            futureBall = input.ballPrediction.ballAtTime(input.car.position.minus(input.ball.position).magnitude()/input.car.velocity.minus(input.ball.velocity).magnitude());
        }*/
        final CarData futureCar = input.statePrediction.carsAtTime(actualTimeOfBallBounce).get(input.playerIndex);

        Vector3 futureDestination = futureBall.position.plus(futureBall.position.minus(ballDestination).scaledToMagnitude(85));

        groundOrientationController.setDestination(futureDestination);
        groundOrientationController.updateOutput(input);

        speedToReach = input.car.position.minus(futureDestination).magnitude()/actualTimeOfBallBounce;
        drivingSpeedController.setSpeed(speedToReach);
        drivingSpeedController.updateOutput(input);

        if(speedToReach > 1400) {
            final double carSpeed = input.car.velocity.magnitude();
            // output.boost(carSpeed > 1300 && carSpeed < speedToReach && speedToReach < 2300);
        }

        output.boost(speedToReach > 1410 && input.car.velocity.magnitude() < speedToReach && input.car.orientation.noseVector.dotProduct(ballDestination) > 0);

        if(input.car.position.minus(input.ball.position).magnitude() < 180) {
            dribbleController.updateOutput(input);
        }

        aerialOrientationHandler.setRollOrientation(new Vector3(0, 0, 10000));
        aerialOrientationHandler.setDestination(input.ball.position);
        aerialOrientationHandler.updateOutput(input);

        //System.out.println(timeOfBallBounce);
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
    }
}
