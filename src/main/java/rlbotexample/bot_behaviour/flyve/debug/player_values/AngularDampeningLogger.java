package rlbotexample.bot_behaviour.flyve.debug.player_values;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.car.ExtendedCarData;
import rlbotexample.output.BotOutput;
import util.math.linear_transform.LinearApproximator;
import util.math.vector.Vector3;

public class AngularDampeningLogger extends FlyveBot {

    public static final double PITCH_DAMPENING = -2.98883270263274; // seconds^-1
    public static final double YAW_DAMPENING = -1.96617598616866;   // seconds^-1
    public static final double ROLL_DAMPENING = -5.09921829072296;  // seconds^-1

    Vector3 lastSpin;
    Vector3 spin;
    Vector3 angularDampening;

    LinearApproximator linearApproximatorForPitch;

    public AngularDampeningLogger() {
        lastSpin = new Vector3();
        spin = new Vector3();
        angularDampening = new Vector3();
        linearApproximatorForPitch = new LinearApproximator(this::pitchAccelerationFromInput, -1, 1, 40);
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        //ExtendedCarData carData = input.allCars.get(1-input.playerIndex);
        ExtendedCarData carData = input.car;

        lastSpin = spin;
        spin = carData.spin.toFrameOfReference(carData.orientation);
        angularDampening = spin.minus(lastSpin).scaled(1/spin.magnitude());

        output().pitch(linearApproximatorForPitch.inverse(-10 - spin.dotProduct(carData.orientation.rightVector)*PITCH_DAMPENING));

        //System.out.println(spin.magnitude()*PITCH_DAMPENING + pitchAccelerationFromInput(output().pitch()));
        System.out.println(spin.minus(lastSpin).y*60);

        // average dampening (angle/60*s^2) for pitch: 0.0498185372543619
        // average dampening (angle/60*s^2) for yaw:   0.0327695997694777
        // average dampening (angle/60*s^2) for roll:  0.0849869715120493


        return output();
    }

    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        //ExtendedCarData carData = input.allCars.get(1-input.playerIndex);
        ExtendedCarData carData = input.car;

        //renderer.drawLine3d(Color.GREEN, angularDampening.scaled(100).plus(new Vector3(0, 0, 100)).toFlatVector(), new Vector3(0, 0, 100).toFlatVector());
    }

    public double pitchAccelerationFromInput(double x) {
        if(x < 0) {
            x = -x;
            return -(-233.95*x*x*x*x*x + 385.9*x*x*x*x - 164.48*x*x*x + 45.535*x*x + 10.918*x);
        }
        return -233.95*x*x*x*x*x + 385.9*x*x*x*x - 164.48*x*x*x + 45.535*x*x + 10.918*x;
        //return 40.778*x*x + 5.1917*x + 0.3106;
    }

    /*
    private Vector3 angularVelocity(Vector3 spin, CarOrientation orientation, double deltaTime) {
        Vector3 localSpin = spin.toFrameOfReference(orientation);

        double pitchSpin = localSpin.x;
        double yawSpin = localSpin.z;
        double rollSpin = localSpin.y;

        double predictedPitchSpin = pitchSpin * Math.exp(PITCH_DAMPENING * deltaTime);
        double predictedYawSpin = yawSpin * Math.exp(YAW_DAMPENING * deltaTime);
        double predictedRollSpin = rollSpin * Math.exp(ROLL_DAMPENING * deltaTime);

        return new Vector3(predictedPitchSpin, predictedRollSpin, predictedYawSpin).matrixRotation(orientation).scaled(-1);
    }*/

    /*
    private CarOrientation angularOrientation(Vector3 spin, CarOrientation orientation, double deltaTime) {
        double pitchSpin = spin.projectOnto(orientation.rightVector).magnitude();
        double yawSpin = spin.projectOnto(orientation.roofVector).magnitude();
        double rollSpin = spin.projectOnto(orientation.noseVector).magnitude();

        double predictedPitchDeltaAngle = pitchSpin * Math.exp(PITCH_DAMPENING * deltaTime)/PITCH_DAMPENING;
        double predictedYawDeltaAngle = yawSpin * Math.exp(YAW_DAMPENING * deltaTime)/YAW_DAMPENING;
        double predictedRollDeltaAngle = rollSpin * Math.exp(ROLL_DAMPENING * deltaTime)/ROLL_DAMPENING;

        Vector3 rotator = orientation.rightVector.scaled(predictedPitchSpin)
                .plus(orientation.roofVector).scaled(predictedYawSpin)
                .plus(orientation.noseVector).scaled(predictedRollSpin);

        return new CarOrientation(orientation.noseVector.rotate());
    }*/
}
