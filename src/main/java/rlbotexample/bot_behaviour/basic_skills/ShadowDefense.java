package rlbotexample.bot_behaviour.basic_skills;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.car_destination.CarDestination;
import rlbotexample.bot_behaviour.panbot.BotBehaviour;
import rlbotexample.input.dynamic_data.DataPacket;

public class ShadowDefense extends SkillController {
    private CarDestination desiredDestination;
    private BotBehaviour bot;

    public ShadowDefense(CarDestination desiredDestination, BotBehaviour bot) {
        this.desiredDestination = desiredDestination;
        this.bot = bot;
    }

    @Override
    public void updateOutput(DataPacket input) {

    }