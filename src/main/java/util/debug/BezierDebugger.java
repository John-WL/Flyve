package util.debug;

import rlbot.render.Renderer;
import util.bezier_curve.PathIterator;
import util.bezier_curve.CurveSegment;
import util.vector.Vector3;

import java.awt.*;

public class BezierDebugger {

    private static final double CURVE_LENGTH_INCREMENT = 1;

    public static void renderPath(CurveSegment myPath, Color color, Renderer renderer) {
        PathIterator bezierIterator = new PathIterator(myPath, myPath.curveLength(50)/300, 10);
        Vector3 testLastPosition;
        Vector3 testCurrentPosition = myPath.interpolate(0);

        while(bezierIterator.hasNext()) {
            testLastPosition = testCurrentPosition;
            testCurrentPosition = bezierIterator.next();
            renderer.drawLine3d(color, testLastPosition, testCurrentPosition);
        }
    }
}
