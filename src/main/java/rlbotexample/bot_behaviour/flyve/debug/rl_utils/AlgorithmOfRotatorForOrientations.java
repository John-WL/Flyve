package rlbotexample.bot_behaviour.flyve.debug.rl_utils;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation.AerialOrientationController3;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.car.Orientation;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;

import java.awt.*;

public class AlgorithmOfRotatorForOrientations extends FlyveBot {

    private Orientation orientation;
    private Vector3 rotator;

    //private AerialOrientationController3 aerialOrientationController;

    public AlgorithmOfRotatorForOrientations() {
        this.orientation = new Orientation(new Vector3(1, 0, 0), Vector3.UP_VECTOR);

        //aerialOrientationController = new AerialOrientationController3(this);
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        rotator = input.allCars.get(1-input.playerIndex).orientation.findRotatorToRotateTo(orientation);

        //aerialOrientationController.setNoseOrientation(input.allCars.get(1-input.playerIndex).position.minus(input.car.position));
        //aerialOrientationController.setRollOrientation(new Vector3(0, 0, 1));

        //aerialOrientationController.updateOutput(input);

        //output().boost(true);

        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        renderer.drawLine3d(Color.GREEN, rotator.scaled(300).plus(new Vector3(0, 0, 300)), new Vector3(0, 0, 300));

        renderer.drawLine3d(Color.red, input.allCars.get(1-input.playerIndex).orientation.roofVector.rotate(rotator).scaled(300).plus(new Vector3(0, 0, 300)), new Vector3(0, 0, 300));

        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderCross(new Vector3(300, 0, 0), Color.red);
    }
}
