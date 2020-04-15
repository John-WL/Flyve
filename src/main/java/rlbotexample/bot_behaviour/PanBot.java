package rlbotexample.bot_behaviour;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.bot_controllers.basic_skills.Dribble;
import rlbotexample.bot_behaviour.bot_controllers.basic_skills.DriveToDestination;
import rlbotexample.bot_behaviour.bot_controllers.basic_skills.FlyToDestination;
import rlbotexample.bot_behaviour.bot_controllers.basic_skills.OutputUpdater;
import rlbotexample.bot_behaviour.car_destination.CarDestination;
import rlbotexample.bot_behaviour.path.PathGenerator;
import rlbotexample.game_situation.GameSituation;
import rlbotexample.game_situation.GroundDribbleSetup1;
import rlbotexample.game_situation.UnhandledGameState;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;

// Pan is an abbreviation for PATCHES ARE NEEDED!
public class PanBot extends BotBehaviour {

    private CarDestination desiredDestination;
    private OutputUpdater outputUpdater;
    private PanBotFpsLogger botFpsLogger;
    private GameSituation gameSituation;
    //private OutputUpdater driveToDestination;
    //private OutputUpdater flyToDestination;

    public PanBot() {
        gameSituation = new UnhandledGameState();
        desiredDestination = new CarDestination();
        PathGenerator.dummyPath(desiredDestination);
        //driveToDestination = new DriveToDestination(desiredDestination, this);
        //flyToDestination = new FlyToDestination(desiredDestination, this);
        outputUpdater = new Dribble(desiredDestination, this);
        botFpsLogger = new PanBotFpsLogger(outputUpdater, desiredDestination);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        // game situations handling!
        // simple ground shots, aerial setups, defense setups, etc...
        // It helps to setup the game state so we can tweak specific bot behaviours
        // before implementing the meta game strats.
        if(gameSituation.isGameStateElapsed()) {
            //gameSituation = new GroundDribbleSetup1();
        }

        /*
        // make sure the path is always up to date with the data packet
        if(input.car.position.y > input.ball.position.y) {
            if(Math.abs(desiredDestination.getThrottleDestination().y) > 5000) {
                PathGenerator.stupidBallChasePath(desiredDestination, input);
            }
            PathGenerator.ballChasePredictionPath(desiredDestination, input);
        }
        else {
            PathGenerator.netPositionPathGenerator(desiredDestination, input);
        }
        */
        PathGenerator.randomGroundPath2(desiredDestination, input);

        // bot's desired position advances one step
        desiredDestination.advanceOneStep(input);

        /*
        if(2*desiredDestination.getThrottleDestination().z + input.ball.velocity.z < 500 ||
            input.car.position.minus(input.ball.position).flatten().magnitude() > input.ball.position.z) {
            outputUpdater = driveToDestination;
        }
        else {
            outputUpdater = flyToDestination;
        }
        */

        // do the thing
        outputUpdater.setupAndUpdateOutputs(input);

        // return the calculated bot output
        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        PanBotFpsLogger botFpsLogger = this.botFpsLogger;

        botFpsLogger.displayDebugLines(renderer, input);

        outputUpdater.debug(renderer, input);

        botFpsLogger.displayFpsCounter(renderer, currentFps);
        botFpsLogger.displayAvgFps(renderer, averageFps);
        botFpsLogger.displayMsPerFrame(renderer, botExecutionTime);
    }
}
