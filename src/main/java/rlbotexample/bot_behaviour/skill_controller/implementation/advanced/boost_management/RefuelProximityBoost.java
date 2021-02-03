package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.boost_management;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.input.dynamic_data.boost.BoostManager;
import rlbotexample.input.dynamic_data.boost.BoostPad;
import rlbotexample.input.dynamic_data.DataPacket;
import util.math.vector.Vector3;

import java.util.ArrayList;
import java.util.List;

public class RefuelProximityBoost extends SkillController {

    private BotBehaviour bot;
    private SkillController driveToDestination;

    public RefuelProximityBoost(BotBehaviour bot) {
        this.bot = bot;
    }

    @Override
    public void updateOutput(DataPacket input) {
        Vector3 playerPosition = input.car.position;
        Vector3 playerNoseOrientation = input.car.orientation.noseVector;

        // regroup all boost pads in one single list
        List<BoostPad> boostPads = new ArrayList<>();
        boostPads.addAll(BoostManager.bigBoosts);
        boostPads.addAll(BoostManager.smallBoosts);

        // get only those that are not taken
        List<BoostPad> notTakenBoostPads = new ArrayList<>();
        for (BoostPad boostPad : boostPads) {
            if (boostPad.isActive) {
                notTakenBoostPads.add(boostPad);
            }
        }

        List<BoostPad> withinRangePads = new ArrayList<>();
        // get all boost pads within desired off-trajectory range
        // (where with respect to the player car do you think it's
        // alright to change slightly the current trajectory so we
        // take a boost while going to destination?)
        for(BoostPad pad: notTakenBoostPads) {
            if(pad.location.minus(playerPosition).magnitude() < 600
                    && Math.abs(playerNoseOrientation.flatten().correctionAngle(pad.location.flatten())) < Math.PI/((input.car.velocity.magnitude()*6/2300) + 4)) {
                withinRangePads.add(pad);
            }
        }

        // get closest in-range pad
        BoostPad closestNotTakenPad = null;
        double distanceOfClosest = Double.MAX_VALUE;
        for (BoostPad withinRangePad : withinRangePads) {
            if (withinRangePad.location.minus(playerPosition).magnitude() < distanceOfClosest) {
                distanceOfClosest = withinRangePad.location.minus(playerPosition).magnitude();
                closestNotTakenPad = withinRangePad;
            }
        }

        // if there were no pad in range, then don't do anything...
        // if there was at least one pad in range, then go to it.
        if(closestNotTakenPad != null) {

            // got to destination
            driveToDestination.setupAndUpdateOutput(input);
            bot.output().boost(false);
            bot.output().jump(false);
        }
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
    }
}
