package rlbotexample.input.geometry;

import util.parameter_configuration.ObjFileReader;
import util.shapes.Sphere;
import util.shapes.Triangle3D;
import util.shapes.meshes.Mesh3D;
import util.shapes.meshes.MeshSplitter3D;
import util.vector.Vector3;

public class StandardMapSplitMesh {

    public static final MeshSplitter3D STANDARD_MAP_MESH = new MeshSplitter3D(ObjFileReader.loadMeshFromFile(ObjFileReader.STANDARD_MAP_MESH_GEOMETRY_PATH));

    public Vector3 getCollisionNormalOrElse(final Sphere sphere, Vector3 defaultNormal) {
        final Triangle3D closestTriangleToBall = STANDARD_MAP_MESH.getClosestTriangle(sphere);
        final Vector3 distanceBetweenSphereCenterAndMesh = sphere.center.projectOnto(closestTriangleToBall).minus(sphere.center);

        if(distanceBetweenSphereCenterAndMesh.magnitudeSquared() < sphere.radius * sphere.radius ) {
            return closestTriangleToBall.getNormal().scaled(-1);
        }

        return defaultNormal;
    }
}
