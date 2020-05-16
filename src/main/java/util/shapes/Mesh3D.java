package util.shapes;

import util.vector.Vector3;

import java.util.List;

public class Mesh3D {

    private final List<Triangle3D> triangleList;

    public Mesh3D(final List<Triangle3D> triangleList) {
        this.triangleList = triangleList;
    }

    public Triangle3D getClosestTriangle(final Sphere sphere) {
        final Vector3 sphereCenter = sphere.center;
        Triangle3D closestTriangle = new Triangle3D();
        double bestDistanceWithTriangleSquared = Double.MAX_VALUE;

        for(Triangle3D triangle: triangleList) {
            final Vector3 projectedPointOnTriangle = sphereCenter.projectOnto(triangle);
            if(projectedPointOnTriangle.minus(sphereCenter).magnitudeSquared() < bestDistanceWithTriangleSquared) {
                closestTriangle = triangle;
                bestDistanceWithTriangleSquared = projectedPointOnTriangle.minus(sphereCenter).magnitudeSquared();
            }
        }

        return closestTriangle;
    }
}
