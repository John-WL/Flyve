package rlbotexample;

import parameter_search.air_dribbling.AirDribbleParameterSearcher;
import rlbot.Bot;
import rlbot.manager.BotManager;
import rlbot.pyinterop.SocketServer;

public class SamplePythonInterface extends SocketServer {

    public SamplePythonInterface(int port, BotManager botManager) {
        super(port, botManager);
    }

    protected Bot initBot(int index, String botType, int team) {
        return new SampleBot(index, new AirDribbleParameterSearcher());
    }
}
