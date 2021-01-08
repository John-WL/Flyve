package util.state_machine;

import rlbot.render.Renderer;
import rlbotexample.input.dynamic_data.DataPacket;

public interface State {
    void exec(DataPacket input);
    State next(DataPacket input);
    void debug(DataPacket input, Renderer renderer);
}
