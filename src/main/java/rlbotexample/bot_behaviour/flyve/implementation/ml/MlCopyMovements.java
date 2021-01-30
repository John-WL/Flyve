package rlbotexample.bot_behaviour.flyve.implementation.ml;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.car.CarOrientation;
import rlbotexample.output.BotOutput;
import util.game_constants.RlConstants;
import util.math.vector.Vector2;
import util.math.vector.Vector3;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.atomic.AtomicBoolean;

public class MlCopyMovements extends FlyveBot {


    private Vector3 previousVelocity;
    private Vector3 previousSpin;
    private OOfNGeneralLinearApproximator botFunction;

    public MlCopyMovements() {
        botFunction = new OOfNGeneralLinearApproximator();

        previousVelocity = new Vector3();
        previousSpin = new Vector3();
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {


        // gathering training data
        float trainingBotBoostAmount = (float)input.allCars.get(1-input.playerIndex).boost;
        Vector3 trainingBotPosition = input.allCars.get(1-input.playerIndex).position.scaled(-1);
        Vector3 trainingBotVelocity = input.allCars.get(1-input.playerIndex).velocity.scaled(-1);
        CarOrientation trainingBotOrientation = new CarOrientation(
                input.allCars.get(1-input.playerIndex).orientation.noseVector.scaled(-1),
                input.allCars.get(1-input.playerIndex).orientation.roofVector.scaled(-1));
        Vector3 trainingBotSpin = input.allCars.get(1-input.playerIndex).spin.scaled(-1);

        Vector3 trainingBallPosition = input.ball.position.scaled(-1);
        Vector3 trainingBallVelocity = input.ball.velocity.scaled(-1);
        Vector3 trainingBallSpin = input.ball.spin.scaled(-1);

        float trainingOpponentBoostAmount = (float)input.car.boost;
        Vector3 trainingOpponentPosition = input.car.position.scaled(-1);
        Vector3 trainingOpponentVelocity = input.car.velocity.scaled(-1);
        CarOrientation trainingOpponentOrientation = new CarOrientation(
                input.car.orientation.noseVector.scaled(-1),
                input.car.orientation.roofVector.scaled(-1));
        Vector3 trainingOpponentSpin = input.car.spin.scaled(-1);

        Vector2 wVec = input.allCars.get(1-input.playerIndex).hasWheelContact ?
                input.allCars.get(1-input.playerIndex).velocity
                        .minus(previousVelocity)
                        .dotProduct(input.allCars.get(1-input.playerIndex).orientation.noseVector) > 0 ?
                        new Vector2(1, 0) : new Vector2() :
                input.allCars.get(1-input.playerIndex).spin.minus(previousSpin)
                        .toFrameOfReference(input.allCars.get(1-input.playerIndex).orientation)
                        .y * RlConstants.BOT_REFRESH_RATE > 1 ?
                        new Vector2(1, 0) : new Vector2();
        Vector2 sVec = input.allCars.get(1-input.playerIndex).hasWheelContact ?
                input.allCars.get(1-input.playerIndex).velocity
                        .minus(previousVelocity)
                        .dotProduct(input.allCars.get(1-input.playerIndex).orientation.noseVector)
                        * RlConstants.BOT_REFRESH_RATE < -1000 ?
                        new Vector2(-1, 0) : new Vector2() :
                input.allCars.get(1-input.playerIndex).spin
                        .minus(previousSpin)
                        .toFrameOfReference(input.allCars.get(1-input.playerIndex).orientation)
                        .y * RlConstants.BOT_REFRESH_RATE < -1 ?
                        new Vector2(-1, 0) : new Vector2();

        System.out.println(sVec);

        Vector2 aVec = new Vector2();
        Vector2 dVec = new Vector2();

        Vector2 trainingWasdMovements = wVec.plus(aVec).plus(sVec).plus(dVec);

        if(input.car.elapsedSeconds % 1 < 0.1)
        botFunction.addSamplePoint(
                new OOfNGeneralLinearApproximator.Input(
                        trainingBotBoostAmount/100,
                        trainingBotPosition.x/(float)RlConstants.WALL_DISTANCE_X, trainingBotPosition.y/(float)RlConstants.WALL_DISTANCE_Y, trainingBotPosition.z/(float)RlConstants.CEILING_HEIGHT,
                        trainingBotVelocity.x/(float)RlConstants.CAR_MAX_SPEED, trainingBotVelocity.y/(float)RlConstants.CAR_MAX_SPEED, trainingBotVelocity.z/(float)RlConstants.CAR_MAX_SPEED,
                        trainingBotOrientation.noseVector.x, trainingBotOrientation.noseVector.y, trainingBotOrientation.noseVector.z,
                        trainingBotOrientation.roofVector.x, trainingBotOrientation.roofVector.y, trainingBotOrientation.roofVector.z,
                        trainingBotSpin.x/(float)RlConstants.BALL_MAX_SPIN, trainingBotSpin.y/(float)RlConstants.BALL_MAX_SPIN, trainingBotSpin.z/(float)RlConstants.BALL_MAX_SPIN,

                        trainingBallPosition.x/(float)RlConstants.WALL_DISTANCE_X, trainingBallPosition.y/(float)RlConstants.WALL_DISTANCE_Y, trainingBallPosition.z/(float)RlConstants.CEILING_HEIGHT,
                        trainingBallVelocity.x/(float)RlConstants.BALL_MAX_SPEED, trainingBallVelocity.y/(float)RlConstants.BALL_MAX_SPEED, trainingBallVelocity.z/(float)RlConstants.BALL_MAX_SPEED,
                        trainingBallSpin.x/(float)RlConstants.BALL_MAX_SPIN, trainingBallSpin.y/(float)RlConstants.BALL_MAX_SPIN, trainingBallSpin.z/(float)RlConstants.BALL_MAX_SPIN,

                        trainingOpponentBoostAmount/100,
                        trainingOpponentPosition.x/(float)RlConstants.WALL_DISTANCE_X, trainingOpponentPosition.y/(float)RlConstants.WALL_DISTANCE_Y, trainingOpponentPosition.z/(float)RlConstants.CEILING_HEIGHT,
                        trainingOpponentVelocity.x/(float)RlConstants.CAR_MAX_SPEED, trainingOpponentVelocity.y/(float)RlConstants.CAR_MAX_SPEED, trainingOpponentVelocity.z/(float)RlConstants.CAR_MAX_SPEED,
                        trainingOpponentOrientation.noseVector.x, trainingOpponentOrientation.noseVector.y, trainingOpponentOrientation.noseVector.z,
                        trainingOpponentOrientation.roofVector.x, trainingOpponentOrientation.roofVector.y, trainingOpponentOrientation.roofVector.z,
                        trainingOpponentSpin.x/(float)RlConstants.BALL_MAX_SPIN, trainingOpponentSpin.y/(float)RlConstants.BALL_MAX_SPIN, trainingOpponentSpin.z/(float)RlConstants.BALL_MAX_SPIN
                ),
                new OOfNGeneralLinearApproximator.Output(
                        trainingWasdMovements.x, trainingWasdMovements.y,
                        0, 0, 0, 0
                        /*trainingIsBoosting,
                        trainingIsJumping,
                        trainingIsDrifting,
                        trainingRollDirection*/
                )
        );

        // gathering bot data
        //boolean canFirstJump = input.car.hasFirstJump;    // unsure if we need those at all
        //boolean canSecondJump = input.car.hasSecondJump;

        float botBoostAmount = (float)input.car.boost;
        Vector3 botPosition = input.car.position;
        Vector3 botVelocity = input.car.velocity;
        CarOrientation botOrientation = input.car.orientation;
        Vector3 botSpin = input.car.spin;

        Vector3 ballPosition = input.ball.position;
        Vector3 ballVelocity = input.ball.velocity;
        Vector3 ballSpin = input.ball.spin;

        float opponentBoostAmount = (float)input.allCars.get(1-input.playerIndex).boost;
        Vector3 opponentPosition = input.allCars.get(1-input.playerIndex).position;
        Vector3 opponentVelocity = input.allCars.get(1-input.playerIndex).velocity;
        CarOrientation opponentOrientation = input.allCars.get(1-input.playerIndex).orientation;
        Vector3 opponentSpin = input.allCars.get(1-input.playerIndex).spin;


        if(botFunction.getSizeOfDataset() > 100) {
            OOfNGeneralLinearApproximator.Output keyboardOutput = botFunction.process(
                    new OOfNGeneralLinearApproximator.Input(
                            botBoostAmount/100,
                            botPosition.x/(float)RlConstants.WALL_DISTANCE_X, botPosition.y/(float)RlConstants.WALL_DISTANCE_Y, botPosition.z/(float)RlConstants.CEILING_HEIGHT,
                            botVelocity.x/(float)RlConstants.CAR_MAX_SPEED, botVelocity.y/(float)RlConstants.CAR_MAX_SPEED, botVelocity.z/(float)RlConstants.CAR_MAX_SPEED,
                            botOrientation.noseVector.x, botOrientation.noseVector.y, botOrientation.noseVector.z,
                            botOrientation.roofVector.x, botOrientation.roofVector.y, botOrientation.roofVector.z,
                            botSpin.x/(float)RlConstants.BALL_MAX_SPIN, botSpin.y/(float)RlConstants.BALL_MAX_SPIN, botSpin.z/(float)RlConstants.BALL_MAX_SPIN,

                            ballPosition.x/(float)RlConstants.WALL_DISTANCE_X, ballPosition.y/(float)RlConstants.WALL_DISTANCE_Y, ballPosition.z/(float)RlConstants.CEILING_HEIGHT,
                            ballVelocity.x/(float)RlConstants.BALL_MAX_SPEED, ballVelocity.y/(float)RlConstants.BALL_MAX_SPEED, ballVelocity.z/(float)RlConstants.BALL_MAX_SPEED,
                            ballSpin.x/(float)RlConstants.BALL_MAX_SPIN, ballSpin.y/(float)RlConstants.BALL_MAX_SPIN, ballSpin.z/(float)RlConstants.BALL_MAX_SPIN,

                            opponentBoostAmount,
                            opponentPosition.x/(float)RlConstants.WALL_DISTANCE_X, opponentPosition.y/(float)RlConstants.WALL_DISTANCE_Y, opponentPosition.z/(float)RlConstants.CEILING_HEIGHT,
                            opponentVelocity.x/(float)RlConstants.CAR_MAX_SPEED, opponentVelocity.y/(float)RlConstants.CAR_MAX_SPEED, opponentVelocity.z/(float)RlConstants.CAR_MAX_SPEED,
                            opponentOrientation.noseVector.x, opponentOrientation.noseVector.y, opponentOrientation.noseVector.z,
                            opponentOrientation.roofVector.x, opponentOrientation.roofVector.y, opponentOrientation.roofVector.z,
                            opponentSpin.x/(float)RlConstants.BALL_MAX_SPIN, opponentSpin.y/(float)RlConstants.BALL_MAX_SPIN, opponentSpin.z/(float)RlConstants.BALL_MAX_SPIN
                    )
            );

            output().throttle(keyboardOutput.get(0));
            output().steer(keyboardOutput.get(1));
            output().boost(keyboardOutput.get(2) > 0.5);
            output().jump(keyboardOutput.get(3) > 0.5);
            output().drift(keyboardOutput.get(4) > 0.5);

            output().pitch(-keyboardOutput.get(0));
            output().yaw(!output().drift() ? keyboardOutput.get(1) : 0);
            output().roll((output().drift() ? keyboardOutput.get(1) : 0) + keyboardOutput.get(5));
        }

        previousVelocity = input.allCars.get(1-input.playerIndex).velocity;
        previousSpin = input.allCars.get(1-input.playerIndex).spin;

        // return the calculated bot output
        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
    }
}
