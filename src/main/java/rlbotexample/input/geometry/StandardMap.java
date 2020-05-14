package rlbotexample.input.geometry;

import util.game_constants.RlConstants;
import util.vector.Vector3;

public class StandardMap extends MapMeshGeometry {

    private static final Vector3 GROUND_HIT_NORMAL = new Vector3(0, 0, -1);
    private static final Vector3 CEILING_HIT_NORMAL = new Vector3(0, 0, 1);
    private static final Vector3 POSITIVE_WALL_X_HIT_NORMAL = new Vector3(1, 0, 0);
    private static final Vector3 NEGATIVE_WALL_X_HIT_NORMAL = new Vector3(-1, 0, 0);
    private static final Vector3 POSITIVE_WALL_Y_HIT_NORMAL = new Vector3(0, 1, 0);
    private static final Vector3 NEGATIVE_WALL_Y_HIT_NORMAL = new Vector3(0, -1, 0);

    public Vector3 getHitNormal(Vector3 globalPoint, double bevel) {
        if(globalPoint.plus(GROUND_HIT_NORMAL.scaled(bevel)).z < 0) {
            return GROUND_HIT_NORMAL;
        }
        else if(globalPoint.plus(CEILING_HIT_NORMAL.scaled(bevel)).z > RlConstants.CEILING_HEIGHT) {
            return CEILING_HIT_NORMAL;
        }

        if(globalPoint.plus(POSITIVE_WALL_X_HIT_NORMAL.scaled(bevel)).x > RlConstants.WALL_DISTANCE_X) {
            return POSITIVE_WALL_X_HIT_NORMAL;
        }
        else if(globalPoint.plus(NEGATIVE_WALL_X_HIT_NORMAL.scaled(bevel)).x < -RlConstants.WALL_DISTANCE_X) {
            return NEGATIVE_WALL_X_HIT_NORMAL;
        }

        if(globalPoint.plus(POSITIVE_WALL_Y_HIT_NORMAL.scaled(bevel)).y > RlConstants.WALL_DISTANCE_Y) {
            return POSITIVE_WALL_Y_HIT_NORMAL;
        }
        else if(globalPoint.plus(NEGATIVE_WALL_Y_HIT_NORMAL.scaled(bevel)).y < -RlConstants.WALL_DISTANCE_Y) {
            return NEGATIVE_WALL_Y_HIT_NORMAL;
        }

        return new Vector3();
    }
}
