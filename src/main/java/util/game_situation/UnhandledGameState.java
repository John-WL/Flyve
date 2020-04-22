package util.game_situation;

import util.timer.Timer;

public class UnhandledGameState extends GameSituation {

    public UnhandledGameState() {
        super(new Timer(0));
    }

    @Override
    void loadGameState() {}
}
