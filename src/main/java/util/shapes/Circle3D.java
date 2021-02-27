package util.shapes;

import util.math.vector.Ray3;
import util.math.vector.Vector;
import util.math.vector.Vector3;

public class Circle3D {

    public final Ray3 center;
    public final double radii;

    private final Circle flatCircle;
    private final double height;
    private final Vector3 orientation;

    public Circle3D(Ray3 center, double radii) {
        this.center = center;
        this.radii = radii;

        this.flatCircle = new Circle(center.offset.flatten(), radii);
        this.height = center.offset.z;
        this.orientation = center.direction;
    }

    public Vector3 findPointOnCircle(double rads) {
        final Vector3 localPointOnFlatCircle = new Vector3(flatCircle.findPointOnCircle(rads).minus(flatCircle.center), 0);

        final double angleOfRotation = orientation.angle(Vector3.UP_VECTOR);
        final Vector3 rotationVector = Vector3.UP_VECTOR.crossProduct(orientation).scaledToMagnitude(angleOfRotation);
        final Vector3 localPointOn3DCircle = localPointOnFlatCircle.rotate(rotationVector);
        final Vector3 circleOffset = new Vector3(flatCircle.center, height);

        return localPointOn3DCircle.plus(circleOffset);
    }

    public Vector3 findClosestPointFrom(Vector3 v) {
        final Vector3 centerOffset = new Vector3(flatCircle.center, height);
        final Vector3 localV = v.minus(centerOffset);
        final Vector3 localVProjectedOnLocalCirclePlane = localV.minus(localV.projectOnto(orientation));

        final Vector3 localResult = localVProjectedOnLocalCirclePlane.scaledToMagnitude(flatCircle.radii);


        return localResult.plus(centerOffset);
    }

    // this function does not check for intersections.
    // if you try to pass an intersecting plane as a parameter, it will still compute
    // the point on the circle that is most oriented towards the plane
    public Vector3 findClosestPointFromNonIntersecting(Plane3D plane) {
        Vector3 projection = center.offset.projectOnto(plane);
        return findClosestPointFrom(projection);
    }

    public double findRadsFromClosestPoint(Vector3 v) {
        final Vector3 centerOffset = new Vector3(flatCircle.center, height);
        final Vector3 closestPoint = findClosestPointFrom(v).minus(centerOffset);
        final double angleOfRotation = orientation.angle(Vector3.UP_VECTOR);
        final Vector3 rotationVector = Vector3.UP_VECTOR.crossProduct(orientation).scaledToMagnitude(-angleOfRotation);
        final Vector3 flattenedPointOnCircle = closestPoint.rotate(rotationVector);
        return Math.atan2(flattenedPointOnCircle.y, flattenedPointOnCircle.x);
    }
}
