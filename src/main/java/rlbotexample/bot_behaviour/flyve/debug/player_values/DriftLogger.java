package rlbotexample.bot_behaviour.flyve.debug.player_values;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.ground.GroundTrajectoryFinder;
import rlbotexample.output.BotOutput;
import util.game_constants.RlConstants;
import util.math.vector.Vector;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;

import java.awt.*;

public class DriftLogger extends FlyveBot {

    private Vector3 v;
    private Vector3 lastV;

    private Vector3 spin;
    private Vector3 lastSpin;

    public DriftLogger() {
        this.v = new Vector3();
        this.lastV = new Vector3();

        this.spin = new Vector3();
        this.lastSpin = new Vector3();
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        output().throttle(0.1);
        output().steer(-1);
        output().boost(false);


        return output();
    }

    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);

        /*
        renderer.drawLine3d(Color.GREEN, input.allCars.get(1-input.playerIndex).position.plus(input.allCars.get(1-input.playerIndex).orientation.noseVector.scaled(300)).toFlatVector(), input.allCars.get(1-input.playerIndex).position.toFlatVector());
        renderer.drawLine3d(Color.blue, input.allCars.get(1-input.playerIndex).position.plus(input.allCars.get(1-input.playerIndex).velocity.scaled(1)).toFlatVector(), input.allCars.get(1-input.playerIndex).position.toFlatVector());

        Vector3 r = input.allCars.get(1-input.playerIndex).orientation.rightVector;
        lastV = v;
        v = input.allCars.get(1-input.playerIndex).velocity;
        Vector3 aMicro = r.scaled(-r.dotProduct(v)).scaled(1);
        Vector3 acceleration = v.minus(lastV).scaled(RlConstants.BOT_REFRESH_RATE);
        //System.out.println(aMicro.magnitude() / acceleration.magnitude());
        renderer.drawLine3d(Color.orange, input.allCars.get(1-input.playerIndex).position.plus(aMicro).toFlatVector(), input.allCars.get(1-input.playerIndex).position.toFlatVector());
        renderer.drawLine3d(Color.MAGENTA, input.allCars.get(1-input.playerIndex).position.plus(acceleration).toFlatVector(), input.allCars.get(1-input.playerIndex).position.toFlatVector());

        Vector3 f = input.allCars.get(1-input.playerIndex).orientation.noseVector;
        Vector3 t = input.allCars.get(1-input.playerIndex).orientation.roofVector;
        lastSpin = spin;
        spin = input.allCars.get(1-input.playerIndex).spin;
        Vector3 aTheta = t.scaled(-f.dotProduct(v)).scaled(spin.z/500);
        Vector3 angularAcceleration = spin.minus(lastSpin).scaled(-RlConstants.BOT_REFRESH_RATE);
        System.out.println(aTheta.magnitude() / angularAcceleration.magnitude());
        //renderer.drawLine3d(Color.CYAN, input.allCars.get(1-input.playerIndex).position.plus(aTheta.scaled(100)).toFlatVector(), input.allCars.get(1-input.playerIndex).position.toFlatVector());
        renderer.drawLine3d(Color.pink, input.allCars.get(1-input.playerIndex).position.plus(angularAcceleration.scaled(1000)).toFlatVector(), input.allCars.get(1-input.playerIndex).position.toFlatVector());
*/
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderTrajectory(GroundTrajectoryFinder.getLeftTurningTrajectory(input.car), 3, Color.CYAN);
    }
}
