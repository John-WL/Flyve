package rlbotexample.bot_behaviour.metagame.advanced_gamestate_info;

import rlbotexample.input.dynamic_data.DataPacket;
import util.game_constants.RlConstants;
import util.math.vector.Vector;
import util.math.vector.Vector2;
import util.math.vector.Vector3;

public class OffensiveStateInfo {
    public static Vector3 potentialHitStrength(DataPacket input, int playerIndex) {
        Vector3 xc = input.allCars.get(playerIndex).position;
        Vector3 xb = input.ball.position;
        Vector3 vc = input.allCars.get(playerIndex).velocity;
        Vector3 vb = input.ball.velocity;
        Vector3 x = xb.minus(xc);
        Vector3 v = vb.minus(vc);
        Vector2 noseOrientation = input.allCars.get(playerIndex).orientation.noseVector
                .flatten()
                .normalized();
        Vector3 a = new Vector3(
                noseOrientation.scaled(-RlConstants.ACCELERATION_DUE_TO_BOOST),
                -RlConstants.NORMAL_GRAVITY_STRENGTH);
        Vector3 timeFromApogee = v.scaled(a.inverse());
        return x
                .minus(v
                        .scaled(timeFromApogee.x,
                                timeFromApogee.y,
                                timeFromApogee.z))
                .minus(x.normalized()
                        .scaled(a.scaled(0.5))
                        .scaled(timeFromApogee)
                        .scaled(timeFromApogee))
                .scaled(-1);
    }
}
