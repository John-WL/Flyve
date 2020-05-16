package rlbotexample.bot_behaviour.panbot.debug.ball_prediction;

import rlbot.cppinterop.RLBotDll;
import rlbot.cppinterop.RLBotInterfaceException;
import rlbot.flat.BallPrediction;
import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.input.dynamic_data.BallData;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.prediction.BallPredictionHelper;
import rlbotexample.output.BotOutput;

import java.awt.*;

public class DebugCustomBallPrediction extends PanBot {

    public DebugCustomBallPrediction() {
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);

        // custom ball prediction is purple
        // native one is red

        BallData previousBall = input.ball;
        int divisor = 0;
        for(BallData nextBall: input.ballPrediction.balls) {
            divisor++;
            divisor %= 8;

            if(divisor == 0) {
                renderer.drawLine3d(new Color(6, 0, 229), previousBall.position, nextBall.position);
                previousBall = nextBall;
            }
        }


        try {
            BallPrediction ballPrediction = RLBotDll.getBallPrediction();
            BallPredictionHelper.drawTillMoment(ballPrediction, input.car.elapsedSeconds + 6, Color.red, renderer);
        } catch (RLBotInterfaceException e) {
            e.printStackTrace();
        }

        /*
        Mesh3DBuilder mesh3DBuilder = new Mesh3DBuilder();
        mesh3DBuilder.addVertex(new Vector3(1000, 1000, 300));
        mesh3DBuilder.addVertex(new Vector3(-1000, 1000, 100));
        mesh3DBuilder.addVertex(new Vector3(-1000, -1000, 100));
        mesh3DBuilder.addTriangle(0, 1, 2);

        Mesh3D mesh3D = mesh3DBuilder.build();

        renderer.drawLine3d(Color.red, new Vector3(1000, 1000, 300), new Vector3(-1000, 1000, 100));
        renderer.drawLine3d(Color.red, new Vector3(-1000, 1000, 100), new Vector3(-1000, -1000, 100));
        renderer.drawLine3d(Color.red, new Vector3(-1000, -1000, 100), new Vector3(1000, 1000, 300));

        Vector3 projectedPointOntoTriangle = input.allCars.get(1).position.projectOnto(mesh3D.triangleList.get(0), renderer);

        renderer.drawLine3d(Color.green, input.allCars.get(1).position, projectedPointOntoTriangle);

        //System.out.println(mesh3D.getClosestTriangle(new Sphere(new Vector3(1, -1, 1), 0.5)).point1);
        */
    }
}
