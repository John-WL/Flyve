package rlbotexample.bot_behaviour.flyve.debug.ball_prediction;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.input.dynamic_data.ball.BallData;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.car.CarData;
import rlbotexample.input.geometry.StandardMap;
import rlbotexample.input.geometry.StandardMapSplitMesh;
import rlbotexample.input.prediction.gamestate_prediction.GameStatePrediction;
import rlbotexample.input.prediction.gamestate_prediction.ball.RawBallTrajectory;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;
import util.shapes.Triangle3D;
import util.shapes.meshes.Mesh3D;
import util.shapes.meshes.MeshSplitter3D;

import java.awt.*;
import java.util.stream.Collectors;

public class DebugCustomBallPrediction extends FlyveBot {

    private double timePrediction;
    private Vector3 predictedPosition;

    public DebugCustomBallPrediction() {
        timePrediction = 0;
        predictedPosition = new Vector3();
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);

        /*
        if(System.currentTimeMillis()/1000.0 % 4 < 0.1) {
            timePrediction = input.car.elapsedSeconds + 4;
        }*/
        // native one is red
        //renderer.drawLine3d(Color.cyan, RawBallTrajectory.trajectory.compute(timePrediction - input.car.elapsedSeconds), input.allCars.get(1-input.playerIndex).position);

        // my ball prediction
        //predictedPosition = input.statePrediction.ballAtTime(timePrediction - input.car.elapsedSeconds).position;

        // custom ball prediction is purple

        BallData previousBall = input.ball;
        int divisor = 0;
        for(BallData nextBall: input.statePrediction.balls) {
            divisor++;
            divisor %= 8;

            if(divisor == 0) {
                renderer.drawLine3d(new Color(229, 0, 229), previousBall.position.toFlatVector(), nextBall.position.toFlatVector());
                previousBall = nextBall;
            }
        }

        //renderer.drawLine3d(Color.blue, predictedPosition, input.allCars.get(1-input.playerIndex).position);

        /*GameStatePrediction advancedBallPrediction = new GameStatePrediction(input.ball, input.allCars.stream().map(car -> (CarData)car).collect(Collectors.toList()), 0.01,120);

        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        for(Mesh3D mesh: MeshSplitter3D.meshRegions) {
            if(mesh != null) {
                for (Triangle3D triangle : mesh.triangleList) {
                    renderer.drawLine3d(new Color(130, 207, 192), triangle.getCenterPosition(), triangle.getCenterPosition().plus(triangle.getNormal().scaledToMagnitude(100)));
                    shapeRenderer.renderTriangle(triangle, Color.CYAN);
                }
            }
        }*/

        // native ball prediction
        /*try {
            BallPrediction ballPrediction = RLBotDll.getBallPrediction();
            NativeBallPredictionRenderer.drawTillMoment(ballPrediction, input.car.elapsedSeconds + 6, Color.red, renderer);
        } catch (RLBotInterfaceException e) {
            e.printStackTrace();
        }*/

        // mesh splitter

        //System.out.println(MeshSplitter3D.MESH_REGIONS);

        /*for(int i = 0; i < 27; i++) {
            final double x = ((int) (i % 3) - 1) * MeshSplitter3D.SPLIT_SIZE;
            final double y = ((int) ((i / 3) % 3) - 1) * MeshSplitter3D.SPLIT_SIZE;
            final double z = ((int) (i / 9) - 1) * MeshSplitter3D.SPLIT_SIZE;
            final Vector3 deltaPosition = new Vector3(x, y, z);

            Vector3 ballCappedPosition = input.ball.position.plus(MeshSplitter3D.OFFSET_POSITION).scaled(1/MeshSplitter3D.SPLIT_SIZE);
            ballCappedPosition = new Vector3((int)ballCappedPosition.x, (int)ballCappedPosition.y, (int)ballCappedPosition.z);

            final HitBox hitBox = new HitBox(ballCappedPosition.scaled(MeshSplitter3D.SPLIT_SIZE).minus(MeshSplitter3D.OFFSET_POSITION).plus(deltaPosition), new Vector3(1, 1, 1).scaled(MeshSplitter3D.SPLIT_SIZE / 2));

            shapeRenderer.renderHitBox(hitBox, Color.green);
        }*/

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

        Vector3 projectedPointOntoTriangle = input.allCars.get(1).position.projectOnto(mesh3D.triangleList.get(0));

        renderer.drawLine3d(Color.green, input.allCars.get(1).position, projectedPointOntoTriangle);
        */

        //System.out.println(mesh3D.getClosestTriangle(new Sphere(new Vector3(1, -1, 1), 0.5)).point1);

    }
}
