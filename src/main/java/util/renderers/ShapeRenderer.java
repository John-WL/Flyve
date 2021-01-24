package util.renderers;

import rlbot.render.Renderer;
import rlbotexample.input.dynamic_data.car.HitBox;
import rlbotexample.input.dynamic_data.RlUtils;
import rlbotexample.input.dynamic_data.ground.GroundTrajectory2DInfo;
import rlbotexample.input.prediction.Trajectory3D;
import rlbotexample.input.prediction.gamestate_prediction.GameStatePrediction;
import util.game_constants.RlConstants;
import util.math.vector.Ray3;
import util.math.vector.Vector2;
import util.shapes.Circle;
import util.shapes.Circle3D;
import util.shapes.Triangle3D;
import util.math.vector.Vector3;

import java.awt.*;

public class ShapeRenderer {

    private final Renderer renderer;

    public ShapeRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    public void renderCross(Vector3 position, Color color) {
        renderer.drawLine3d(color, position.plus(new Vector3(20, 20, 20)), position.plus(new Vector3(-20, -20, -20)));
        renderer.drawLine3d(color, position.plus(new Vector3(-20, 20, 20)), position.plus(new Vector3(20, -20, -20)));
        renderer.drawLine3d(color, position.plus(new Vector3(20, -20, 20)), position.plus(new Vector3(-20, 20, -20)));
        renderer.drawLine3d(color, position.plus(new Vector3(20, 20, -20)), position.plus(new Vector3(-20, -20, 20)));
    }

    public void renderTriangle(Triangle3D triangle, Color color) {
        renderer.drawLine3d(color, triangle.point0, triangle.point1);
        renderer.drawLine3d(color, triangle.point1, triangle.point2);
        renderer.drawLine3d(color, triangle.point2, triangle.point0);
    }

    public void renderCircle(Circle circle, double zOffset, Color color) {
        int amountOfPoints = 100;
        double precision = Math.PI*2/amountOfPoints;
        Vector2 point = circle.findPointOnCircle(0);
        Vector2 previousPoint;

        for(int i = 1; i < amountOfPoints; i++) {
            previousPoint = point;
            point = circle.findPointOnCircle(i*precision);
            renderer.drawLine3d(color, new Vector3(previousPoint, zOffset), new Vector3(point, zOffset));
        }
    }

    public void renderGroundTrajectory2D(GroundTrajectory2DInfo groundTrajectory, double zOffset, Color color) {
        int amountOfPoints = 100;
        double precision = Math.PI*2/amountOfPoints;
        double speed = 1000;
        Vector2 point = groundTrajectory.findPointFromElapsedTimeAndSpeed(0, speed);
        Vector2 previousPoint;

        for(int i = 1; i < amountOfPoints; i++) {
            previousPoint = point;
            point = groundTrajectory.findPointFromElapsedTimeAndSpeed(i*precision, speed);
            renderer.drawLine3d(color, new Vector3(previousPoint, zOffset), new Vector3(point, zOffset));
        }
    }

    public void renderCircle3D(Circle3D circle, Color color) {
        int amountOfPoints = 30;
        double precision = Math.PI*2/amountOfPoints;
        Vector3 point = circle.findPointOnCircle(0);
        Vector3 previousPoint;

        for(int i = 1; i <= amountOfPoints; i++) {
            previousPoint = point;
            point = circle.findPointOnCircle(i*precision);
            renderer.drawLine3d(color, previousPoint, point);
        }
    }

    public void renderTrajectory(Trajectory3D parabola, double amountOfTimeToRender, Color color) {
        Vector3 previousPosition = parabola.compute(0);
        for(int i = 1; i < 40; i++) {
            Vector3 nextPosition = parabola.compute(i*amountOfTimeToRender/40);
            renderer.drawLine3d(color, nextPosition, previousPosition);
            previousPosition = nextPosition;
        }
    }

    public void renderTrajectory(Trajectory3D parabola, double fromTime, double toTime, Color color) {
        Vector3 previousPosition = parabola.compute(fromTime);
        for(int i = 1; i < 20; i++) {
            double timeToCompute = fromTime + ((i/40.0)*(toTime-fromTime));
            Vector3 nextPosition = parabola.compute(timeToCompute);
            renderer.drawLine3d(color, nextPosition, previousPosition);
            previousPosition = nextPosition;
        }
    }

    public void renderBallPrediction(GameStatePrediction ballPrediction, double amountOfTimeToRender, Color color) {
        Vector3 previousPosition = ballPrediction.ballAtTime(0).position;
        for(int i = 1; i < amountOfTimeToRender*RlUtils.BALL_PREDICTION_REFRESH_RATE; i++) {
            Vector3 nextPosition = ballPrediction.ballAtTime(i/RlUtils.BALL_PREDICTION_REFRESH_RATE).position;
            renderer.drawLine3d(color, nextPosition, previousPosition);
            previousPosition = nextPosition;
        }
    }

    public void renderHitBox(HitBox hitBox, Color color) {
        Vector3 opponentNoseOrientation = hitBox.frontOrientation;
        Vector3 opponentRoofOrientation = hitBox.roofOrientation;
        Vector3 opponentRightOrientation = opponentNoseOrientation.crossProduct(opponentRoofOrientation);

        Vector3 hitBoxCorner111 = hitBox.projectPointOnSurface(opponentNoseOrientation.plus(opponentRoofOrientation).plus(opponentRightOrientation).scaled(300).plus(hitBox.centerPosition));
        Vector3 hitBoxCorner110 = hitBox.projectPointOnSurface(opponentNoseOrientation.plus(opponentRoofOrientation).plus(opponentRightOrientation.scaled(-1)).scaled(300).plus(hitBox.centerPosition));
        Vector3 hitBoxCorner101 = hitBox.projectPointOnSurface(opponentNoseOrientation.plus(opponentRoofOrientation.scaled(-1)).plus(opponentRightOrientation).scaled(300).plus(hitBox.centerPosition));
        Vector3 hitBoxCorner100 = hitBox.projectPointOnSurface(opponentNoseOrientation.plus(opponentRoofOrientation.scaled(-1)).plus(opponentRightOrientation.scaled(-1)).scaled(300).plus(hitBox.centerPosition));
        Vector3 hitBoxCorner011 = hitBox.projectPointOnSurface(opponentNoseOrientation.scaled(-1).plus(opponentRoofOrientation).plus(opponentRightOrientation).scaled(300).plus(hitBox.centerPosition));
        Vector3 hitBoxCorner010 = hitBox.projectPointOnSurface(opponentNoseOrientation.scaled(-1).plus(opponentRoofOrientation).plus(opponentRightOrientation.scaled(-1)).scaled(300).plus(hitBox.centerPosition));
        Vector3 hitBoxCorner001 = hitBox.projectPointOnSurface(opponentNoseOrientation.scaled(-1).plus(opponentRoofOrientation.scaled(-1)).plus(opponentRightOrientation).scaled(300).plus(hitBox.centerPosition));
        Vector3 hitBoxCorner000 = hitBox.projectPointOnSurface(opponentNoseOrientation.scaled(-1).plus(opponentRoofOrientation.scaled(-1)).plus(opponentRightOrientation.scaled(-1)).scaled(300).plus(hitBox.centerPosition));

        renderer.drawLine3d(color, hitBoxCorner111, hitBoxCorner110);
        renderer.drawLine3d(color, hitBoxCorner111, hitBoxCorner101);
        renderer.drawLine3d(color, hitBoxCorner111, hitBoxCorner011);

        renderer.drawLine3d(color, hitBoxCorner010, hitBoxCorner011);
        renderer.drawLine3d(color, hitBoxCorner010, hitBoxCorner000);
        renderer.drawLine3d(color, hitBoxCorner010, hitBoxCorner110);

        renderer.drawLine3d(color, hitBoxCorner001, hitBoxCorner000);
        renderer.drawLine3d(color, hitBoxCorner001, hitBoxCorner011);
        renderer.drawLine3d(color, hitBoxCorner001, hitBoxCorner101);

        renderer.drawLine3d(color, hitBoxCorner100, hitBoxCorner101);
        renderer.drawLine3d(color, hitBoxCorner100, hitBoxCorner110);
        renderer.drawLine3d(color, hitBoxCorner100, hitBoxCorner000);
    }

    public void renderChaoticSphere(Vector3 position, Color color) {
        Circle3D circle1 = new Circle3D(new Ray3(position, Vector3.generateRandomVector()), RlConstants.BALL_RADIUS);
        Circle3D circle2 = new Circle3D(new Ray3(position, Vector3.generateRandomVector()), RlConstants.BALL_RADIUS);
        Circle3D circle3 = new Circle3D(new Ray3(position, Vector3.generateRandomVector()), RlConstants.BALL_RADIUS);
        Circle3D circle4 = new Circle3D(new Ray3(position, Vector3.generateRandomVector()), RlConstants.BALL_RADIUS);
        Circle3D circle5 = new Circle3D(new Ray3(position, Vector3.generateRandomVector()), RlConstants.BALL_RADIUS);
        renderCircle3D(circle1, color);
        renderCircle3D(circle2, color);
        renderCircle3D(circle3, color);
        renderCircle3D(circle4, color);
        renderCircle3D(circle5, color);
    }

    public void renderSwerlingSphere(Vector3 position, double radii, Color color) {
        Vector3 rotator1 = new Vector3(1, 0, 0).rotate(new Vector3(0, 1, 0).scaled((System.currentTimeMillis()/500.0)%(Math.PI*2)));
        Vector3 rotator2 = new Vector3(1, 1, 0).rotate(new Vector3(0, 1, 0).scaled((System.currentTimeMillis()/300.0)%(Math.PI*2)));
        Vector3 orientation1 = new Vector3(0, 0, 1).rotate(rotator1.scaled((System.currentTimeMillis()/2000.0)%(Math.PI*2)));
        Vector3 orientation2 = new Vector3(0, 1, 0).rotate(rotator1.scaled((System.currentTimeMillis()/1700.0 + Math.PI/3)%(Math.PI*2)));
        Vector3 orientation3 = new Vector3(1, 3, 0).rotate(rotator2.scaled((System.currentTimeMillis()/1800.0 + Math.PI/7)%(Math.PI*2)));
        Vector3 orientation4 = new Vector3(1, 0, 2.5).rotate(rotator2.scaled((System.currentTimeMillis()/1300.0 + 3*(Math.PI/13))%(Math.PI*2)));
        Vector3 orientation5 = new Vector3(1, 2, 3).rotate(rotator2.scaled((System.currentTimeMillis()/1200.0 + 5*(Math.PI/17))%(Math.PI*2)));
        Vector3 orientation6 = new Vector3(10, 7, 19).rotate(rotator2.scaled((System.currentTimeMillis()/1500.0 + 7*(Math.PI/31))%(Math.PI*2)));
        Circle3D circle1 = new Circle3D(new Ray3(position, orientation1), radii);
        Circle3D circle2 = new Circle3D(new Ray3(position, orientation2), radii);
        Circle3D circle3 = new Circle3D(new Ray3(position, orientation3), radii);
        Circle3D circle4 = new Circle3D(new Ray3(position, orientation4), radii);
        Circle3D circle5 = new Circle3D(new Ray3(position, orientation5), radii);
        Circle3D circle6 = new Circle3D(new Ray3(position, orientation6), radii);
        renderCircle3D(circle1, color);
        renderCircle3D(circle2, color);
        renderCircle3D(circle3, color);
        renderCircle3D(circle4, color);
        renderCircle3D(circle5, color);
        renderCircle3D(circle6, color);
    }
}
