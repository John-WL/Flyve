package util.renderers;

import rlbot.render.Renderer;
import rlbotexample.input.dynamic_data.HitBox;
import util.shapes.Triangle3D;
import util.vector.Vector3;

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
}
