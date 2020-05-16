package rlbotexample.input.geometry;

import util.game_constants.RlConstants;
import util.parameter_configuration.meshes.ObjFileReader;
import util.shapes.Mesh3D;
import util.shapes.Sphere;
import util.vector.Vector3;

public class NotOptimizedStandardMapMesh {

    private static final Mesh3D STANDARD_MAP_MESH = ObjFileReader.loadMeshFromFile(ObjFileReader.STANDARD_MAP_MESH_GEOMETRY_PATH);

    public Vector3 getCollisionNormalOrElse(final Sphere sphere, Vector3 defaultNormal) {
        final Vector3 distanceBetweenSphereCenterAndMesh = sphere.center.projectOnto(STANDARD_MAP_MESH.getClosestTriangle(sphere)).minus(sphere.center);

        if(distanceBetweenSphereCenterAndMesh.magnitudeSquared() < RlConstants.BALL_RADIUS * RlConstants.BALL_RADIUS) {
            return distanceBetweenSphereCenterAndMesh.normalized();
        }

        return defaultNormal;
    }
}
