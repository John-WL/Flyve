package util.shapes.meshes;

import rlbotexample.input.dynamic_data.HitBox;
import util.game_constants.RlConstants;
import util.shapes.Triangle3D;
import util.vector.Vector3;
import util.vector.Vector3Int;

import java.util.ArrayList;
import java.util.List;

public class MeshSplitter3D {

    private static final double SPLIT_SIZE = RlConstants.BALL_RADIUS;
    private static final double OFFSET_POSITION_X = 1.5*RlConstants.WALL_DISTANCE_X;
    private static final double OFFSET_POSITION_Y = 1.5*RlConstants.WALL_DISTANCE_Y;
    private static final double OFFSET_POSITION_Z = 100;
    private static final Vector3 OFFSET_POSITION = new Vector3(OFFSET_POSITION_X, OFFSET_POSITION_Y, OFFSET_POSITION_Z);

    private final Mesh3D initialMesh;
    private Mesh3D[][][] meshArray;

    public MeshSplitter3D(Mesh3D mesh) {
        this.initialMesh = mesh;
        int lengthX = (int)(6*RlConstants.WALL_DISTANCE_X/SPLIT_SIZE);
        int lengthY = (int)(6*RlConstants.WALL_DISTANCE_Y/SPLIT_SIZE);
        int lengthZ = (int)(2*RlConstants.CEILING_HEIGHT/SPLIT_SIZE);
        meshArray = new Mesh3D[lengthX][lengthY][lengthZ];
        split(mesh);
    }

    public Triangle3D getClosestTriangle(Vector3 position) {
        List<Mesh3D> meshRegions = new ArrayList<>();

        // query the mesh region that's close to the position
        for(int i = 0; i < 27; i++) {
            final double x = ((int)(i % 3) - 1)*SPLIT_SIZE;
            final double y = ((int)(i / 3) - 1)*SPLIT_SIZE;
            final double z = ((int)(i / 9) - 1)*SPLIT_SIZE;
            final Vector3 deltaPosition = new Vector3(x, y, z);

            final Mesh3D meshRegion = queryMeshRegion(position.plus(deltaPosition));
            meshRegions.add(meshRegion);
        }

        // search through that specific region only
        Triangle3D closestTriangle = new Triangle3D();
        double bestDistanceFromTriangleSquared = Double.MAX_VALUE;
        for(Mesh3D mesh: meshRegions) {
            if(mesh != null) {
                for (Triangle3D triangle : mesh.triangleList) {
                    final Vector3 projectedPointOnTriangle = position.projectOnto(triangle);
                    if (projectedPointOnTriangle.minus(position).magnitudeSquared() < bestDistanceFromTriangleSquared) {
                        closestTriangle = triangle;
                        bestDistanceFromTriangleSquared = projectedPointOnTriangle.minus(position).magnitudeSquared();
                    }
                }
            }
        }

        return closestTriangle;
    }

    public Mesh3D queryMeshRegion(Vector3 globalPosition) {
        final Vector3 offsetPosition = globalPosition.plus(OFFSET_POSITION);
        final Vector3 indexVector = offsetPosition.scaled(1/SPLIT_SIZE);
        final Vector3Int index = new Vector3Int((int)indexVector.x, (int)indexVector.y, (int)indexVector.z);

        return meshArray[index.x][index.y][index.z];
    }

    private void split(Mesh3D mesh) {
        for(Triangle3D triangle: mesh.triangleList) {
            final List<Vector3Int> voxelIndexes = rasterizeTriangle3D(triangle);
            for(Vector3Int indexVector: voxelIndexes) {
                if (meshArray[indexVector.x][indexVector.y][indexVector.z] == null) {
                    meshArray[indexVector.x][indexVector.y][indexVector.z] = new Mesh3D();
                }
                meshArray[indexVector.x][indexVector.y][indexVector.z].addTriangleToMesh(triangle);
            }
        }
    }

    // a single triangle can be situated in many indexes at the same time
    // (like pixels, but here it's actually voxels in a sense),
    // so that's why we rasterize here
    // Watch out, this returns INDEXES, and not positions.
    // Duh, we're talking about voxels...
    private List<Vector3Int> rasterizeTriangle3D(Triangle3D triangle) {
        List<Vector3Int> voxels = new ArrayList<>();

        final double smallestPositionX = Math.min(Math.min(triangle.point0.x, triangle.point1.x), triangle.point2.x);
        final double smallestPositionY = Math.min(Math.min(triangle.point0.y, triangle.point1.y), triangle.point2.y);
        final double smallestPositionZ = Math.min(Math.min(triangle.point0.z, triangle.point1.z), triangle.point2.z);
        final double biggestPositionX = Math.max(Math.max(triangle.point0.x, triangle.point1.x), triangle.point2.x);
        final double biggestPositionY = Math.max(Math.max(triangle.point0.y, triangle.point1.y), triangle.point2.y);
        final double biggestPositionZ = Math.max(Math.max(triangle.point0.z, triangle.point1.z), triangle.point2.z);

        final int smallestIndexX = (int)((smallestPositionX+OFFSET_POSITION_X)/SPLIT_SIZE);
        final int smallestIndexY = (int)((smallestPositionY+OFFSET_POSITION_Y)/SPLIT_SIZE);
        final int smallestIndexZ = (int)((smallestPositionZ+OFFSET_POSITION_Z)/SPLIT_SIZE);
        final int biggestIndexX = (int)((biggestPositionX+OFFSET_POSITION_X)/SPLIT_SIZE);
        final int biggestIndexY = (int)((biggestPositionY+OFFSET_POSITION_Y)/SPLIT_SIZE);
        final int biggestIndexZ = (int)((biggestPositionZ+OFFSET_POSITION_Z)/SPLIT_SIZE);

        for(int i = smallestIndexX; i < biggestIndexX+1; i++) {
            for(int j = smallestIndexY; j < biggestIndexY+1; j++) {
                for(int k = smallestIndexZ; k < biggestIndexZ+1; k++) {
                    final Vector3 boxCenterPosition = new Vector3(i, j, k).scaled(SPLIT_SIZE).minus(new Vector3(OFFSET_POSITION_X, OFFSET_POSITION_Y, OFFSET_POSITION_Z));
                    final HitBox voxelHitBox = new HitBox(new Vector3(RlConstants.BALL_RADIUS, RlConstants.BALL_RADIUS, RlConstants.BALL_RADIUS), boxCenterPosition);
                    final Vector3 projectedPointOnTriangle = boxCenterPosition.projectOnto(triangle);
                    final Vector3 projectedPointOnVoxel = voxelHitBox.projectPointOnSurface(projectedPointOnTriangle);
                    if(projectedPointOnTriangle.minus(projectedPointOnVoxel).magnitude() < 0.1) {
                        voxels.add(new Vector3Int(i, j, k));
                    }
                }
            }
        }

        return voxels;
    }
}
