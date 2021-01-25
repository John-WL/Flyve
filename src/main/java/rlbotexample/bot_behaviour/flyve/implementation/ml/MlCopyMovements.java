package rlbotexample.bot_behaviour.flyve.implementation.ml;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.car.CarOrientation;
import rlbotexample.input.dynamic_data.car.Orientation;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector2;
import util.math.vector.Vector3;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.atomic.AtomicBoolean;

public class MlCopyMovements extends FlyveBot implements MouseListener {

    private static volatile boolean leftMouseDown = false;
    private static volatile boolean rightMouseDown = false;
    private GeneralLinearApproximator botFunction;

    public MlCopyMovements() {
        botFunction = new GeneralLinearApproximator();
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

        // gathering user keyboard inputs
        Vector2 trainingWasdMovements = new Vector2();
        double trainingIsDrifting = 0;
        double trainingIsBoosting = 0;
        double trainingIsJumping = 0;

        final AtomicBoolean wPressed = new AtomicBoolean(false);
        final AtomicBoolean aPressed = new AtomicBoolean(false);
        final AtomicBoolean sPressed = new AtomicBoolean(false);
        final AtomicBoolean dPressed = new AtomicBoolean(false);
        final AtomicBoolean shiftPressed = new AtomicBoolean(false);
        final AtomicBoolean qPressed = new AtomicBoolean(false);
        final AtomicBoolean ePressed = new AtomicBoolean(false);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(ke -> {
            synchronized (MlCopyMovements.class) {
                switch (ke.getID()) {
                    case KeyEvent.KEY_PRESSED:
                        if (ke.getKeyCode() == KeyEvent.VK_W) {
                            wPressed.set(true);
                        }
                        if (ke.getKeyCode() == KeyEvent.VK_A) {
                            aPressed.set(true);
                        }
                        if (ke.getKeyCode() == KeyEvent.VK_S) {
                            sPressed.set(true);
                        }
                        if (ke.getKeyCode() == KeyEvent.VK_D) {
                            dPressed.set(true);
                        }
                        if (ke.getKeyCode() == KeyEvent.VK_SHIFT) {
                            shiftPressed.set(true);
                        }
                        if (ke.getKeyCode() == KeyEvent.VK_Q) {
                            qPressed.set(true);
                        }
                        if (ke.getKeyCode() == KeyEvent.VK_E) {
                            ePressed.set(true);
                        }
                        break;

                    case KeyEvent.KEY_RELEASED:
                        if (ke.getKeyCode() == KeyEvent.VK_W) {
                            wPressed.set(false);
                        }
                        if (ke.getKeyCode() == KeyEvent.VK_A) {
                            aPressed.set(false);
                        }
                        if (ke.getKeyCode() == KeyEvent.VK_S) {
                            sPressed.set(false);
                        }
                        if (ke.getKeyCode() == KeyEvent.VK_D) {
                            dPressed.set(false);
                        }
                        if (ke.getKeyCode() == KeyEvent.VK_SHIFT) {
                            shiftPressed.set(false);
                        }
                        if (ke.getKeyCode() == KeyEvent.VK_Q) {
                            qPressed.set(false);
                        }
                        if (ke.getKeyCode() == KeyEvent.VK_E) {
                            ePressed.set(false);
                        }
                        break;
                }
                return false;
            }
        });

        Vector2 wVec = new Vector2();
        Vector2 aVec = new Vector2();
        Vector2 sVec = new Vector2();
        Vector2 dVec = new Vector2();
        double trainingRollDirection = 0;
        synchronized (MlCopyMovements.class) {
            if(wPressed.get()) {
                wVec = new Vector2(1, 0);
            }
        }
        synchronized (MlCopyMovements.class) {
            if(aPressed.get()) {
                aVec = new Vector2(0, 1);
            }
        }
        synchronized (MlCopyMovements.class) {
            if(sPressed.get()) {
                sVec = new Vector2(-1, 0);
            }
        }
        synchronized (MlCopyMovements.class) {
            if(dPressed.get()) {
                dVec = new Vector2(0, -1);
            }
        }
        synchronized (MlCopyMovements.class) {
            if(shiftPressed.get()) {
                trainingIsDrifting = 1;
            }
        }
        synchronized (MlCopyMovements.class) {
            if(leftMouseDown) {
                trainingIsBoosting = 1;
            }
        }
        synchronized (MlCopyMovements.class) {
            if(rightMouseDown) {
                trainingIsJumping = 1;
            }
        }
        synchronized (MlCopyMovements.class) {
            if(qPressed.get()) {
                trainingRollDirection += 1;
            }
        }
        synchronized (MlCopyMovements.class) {
            if(ePressed.get()) {
                trainingRollDirection += -1;
            }
        }
        trainingWasdMovements = wVec.plus(aVec).plus(sVec).plus(dVec);

        botFunction.addSamplePoint(
                new GeneralLinearApproximator.Input(
                        trainingBotBoostAmount,
                        trainingBotPosition.x, trainingBotPosition.y, trainingBotPosition.z,
                        trainingBotVelocity.x, trainingBotVelocity.y, trainingBotVelocity.z,
                        trainingBotOrientation.noseVector.x, trainingBotOrientation.noseVector.y, trainingBotOrientation.noseVector.z,
                        trainingBotOrientation.roofVector.x, trainingBotOrientation.roofVector.y, trainingBotOrientation.roofVector.z,
                        trainingBotSpin.x, trainingBotSpin.y, trainingBotSpin.z,

                        trainingBallPosition.x, trainingBallPosition.y, trainingBallPosition.z,
                        trainingBallVelocity.x, trainingBallVelocity.y, trainingBallVelocity.z,
                        trainingBallSpin.x, trainingBallSpin.y, trainingBallSpin.z,

                        trainingOpponentBoostAmount,
                        trainingOpponentPosition.x, trainingOpponentPosition.y, trainingOpponentPosition.z,
                        trainingOpponentVelocity.x, trainingOpponentVelocity.y, trainingOpponentVelocity.z,
                        trainingOpponentOrientation.noseVector.x, trainingOpponentOrientation.noseVector.y, trainingOpponentOrientation.noseVector.z,
                        trainingOpponentOrientation.roofVector.x, trainingOpponentOrientation.roofVector.y, trainingOpponentOrientation.roofVector.z,
                        trainingOpponentSpin.x, trainingOpponentSpin.y, trainingOpponentSpin.z
                ),
                new GeneralLinearApproximator.Output(
                        trainingWasdMovements.x, trainingWasdMovements.y,
                        trainingIsBoosting,
                        trainingIsJumping,
                        trainingIsDrifting,
                        trainingRollDirection
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
            GeneralLinearApproximator.Output keyboardOutput = botFunction.process(
                    new GeneralLinearApproximator.Input(
                            botBoostAmount,
                            botPosition.x, botPosition.y, botPosition.z,
                            botVelocity.x, botVelocity.y, botVelocity.z,
                            botOrientation.noseVector.x, botOrientation.noseVector.y, botOrientation.noseVector.z,
                            botOrientation.roofVector.x, botOrientation.roofVector.y, botOrientation.roofVector.z,
                            botSpin.x, botSpin.y, botSpin.z,

                            ballPosition.x, ballPosition.y, ballPosition.z,
                            ballVelocity.x, ballVelocity.y, ballVelocity.z,
                            ballSpin.x, ballSpin.y, ballSpin.z,

                            opponentBoostAmount,
                            opponentPosition.x, opponentPosition.y, opponentPosition.z,
                            opponentVelocity.x, opponentVelocity.y, opponentVelocity.z,
                            opponentOrientation.noseVector.x, opponentOrientation.noseVector.y, opponentOrientation.noseVector.z,
                            opponentOrientation.roofVector.x, opponentOrientation.roofVector.y, opponentOrientation.roofVector.z,
                            opponentSpin.x, opponentSpin.y, opponentSpin.z
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

        // return the calculated bot output
        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        synchronized (MlCopyMovements.class) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                leftMouseDown = true;
            }
            if(e.getButton() == MouseEvent.BUTTON2) {
                rightMouseDown = true;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        synchronized (MlCopyMovements.class) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                leftMouseDown = false;
            }
            if(e.getButton() == MouseEvent.BUTTON2) {
                rightMouseDown = false;
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
