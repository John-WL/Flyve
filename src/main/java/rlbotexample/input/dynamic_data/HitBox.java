package rlbotexample.input.dynamic_data;

import rlbot.flat.BoxShape;
import util.vector.Vector3;

public class HitBox {
    public Vector3 centerPosition;
    public Vector3 frontOrientation;
    public Vector3 roofOrientation;

    private Vector3 cornerPosition;

    public HitBox(BoxShape boxShape, rlbot.flat.Vector3 centerOfMassOffset, Vector3 centerPosition, Vector3 frontOrientation, Vector3 roofOrientation) {
        this.cornerPosition = new Vector3(boxShape.length(), boxShape.width(), boxShape.height()).scaled(0.5);
        this.centerPosition = centerPosition.plus(new Vector3(centerOfMassOffset).scaled(-1, 1, 1).matrixRotation(frontOrientation, roofOrientation));
        this.frontOrientation = frontOrientation;
        this.roofOrientation = roofOrientation;
    }

    private HitBox() { }

    public HitBox generateHypotheticalHitBox(Vector3 hypotheticalPosition, Orientation hypotheticalOrientation) {
        HitBox hypotheticalHitBox = new HitBox();
        hypotheticalHitBox.cornerPosition = this.cornerPosition;
        hypotheticalHitBox.centerPosition = hypotheticalPosition;
        hypotheticalHitBox.frontOrientation = hypotheticalOrientation.getNose();
        hypotheticalHitBox.roofOrientation = hypotheticalOrientation.getRoof();

        return hypotheticalHitBox;
    }

    public Vector3 projectPointOnSurface(Vector3 pointToProject) {
        Vector3 localPointToProject = getLocal(pointToProject);

        double newXCoordinate = localPointToProject.x;
        if(localPointToProject.x > cornerPosition.x) {
            newXCoordinate = cornerPosition.x;
        }
        else if(localPointToProject.x < -cornerPosition.x) {
            newXCoordinate = -cornerPosition.x;
        }

        double newYCoordinate = -localPointToProject.y;
        if(localPointToProject.y > cornerPosition.y) {
            newYCoordinate = -cornerPosition.y;
        }
        else if(localPointToProject.y < -cornerPosition.y) {
            newYCoordinate = cornerPosition.y;
        }

        double newZCoordinate = localPointToProject.z;
        if(localPointToProject.z > cornerPosition.z) {
            newZCoordinate = cornerPosition.z;
        }
        else if(localPointToProject.z < -cornerPosition.z) {
            newZCoordinate = -cornerPosition.z;
        }

        return getGlobal(new Vector3(newXCoordinate, newYCoordinate, newZCoordinate));
    }

    private Vector3 getLocal(Vector3 globalPoint) {
        return globalPoint.minus(centerPosition).toFrameOfReference(frontOrientation, roofOrientation);
    }

    private Vector3 getGlobal(Vector3 localPoint) {
        return localPoint.matrixRotation(frontOrientation, roofOrientation).plus(centerPosition);
    }
}
