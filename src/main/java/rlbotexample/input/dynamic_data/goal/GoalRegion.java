package rlbotexample.input.dynamic_data.goal;

import util.math.vector.Ray3;
import util.math.vector.Vector3;

public class GoalRegion {

    public final Vector3 upperLeft;
    public final Vector3 lowerRight;
    public final Ray3 normal;

    public GoalRegion(Vector3 upperLeft, Vector3 lowerRight) {
        this.upperLeft = upperLeft;
        this.lowerRight = lowerRight;
        this.normal = new Ray3(
                upperLeft.plus(lowerRight).scaled(0.5),
                lowerRight.minus(upperLeft).normalized()
                        .crossProduct(Vector3.UP_VECTOR));
    }

    // this function is an oversimplification of the real behaviour because we
    // assume that the goals are actually planes in the XZ direction.
    public Vector3 closestPointOnSurface(Vector3 globalPoint) {
        globalPoint = globalPoint.minus(globalPoint.projectOnto(normal.direction))
                .plus(normal.offset.projectOnto(normal.direction));
        if(lowerRight.x > upperLeft.x) {
            if(globalPoint.x > lowerRight.x) {
                globalPoint = new Vector3(lowerRight.x, globalPoint.y, globalPoint.z);
            }
            else if(globalPoint.x < upperLeft.x) {
                globalPoint = new Vector3(upperLeft.x, globalPoint.y, globalPoint.z);
            }
        }
        else {
            if(globalPoint.x > upperLeft.x) {
                globalPoint = new Vector3(upperLeft.x, globalPoint.y, globalPoint.z);
            }
            else if(globalPoint.x < lowerRight.x) {
                globalPoint = new Vector3(lowerRight.x, globalPoint.y, globalPoint.z);
            }
        }
        if(globalPoint.z > upperLeft.z) {
            globalPoint = new Vector3(globalPoint.x, globalPoint.y, upperLeft.z);
        }
        else if(globalPoint.z < lowerRight.z) {
            globalPoint = new Vector3(globalPoint.x, globalPoint.y, lowerRight.z);
        }

        return globalPoint;
    }
}
