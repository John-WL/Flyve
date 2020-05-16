package util.parameter_configuration.meshes;

import util.parameter_configuration.IOFile;
import util.shapes.Mesh3D;
import util.vector.Vector3;
import util.vector.Vector3Int;

import java.util.Arrays;
import java.util.List;

public class ObjFileReader {

    public static final String MESHES_PATH = "src\\main\\resources\\maps";
    public static final String STANDARD_MAP_MESH_GEOMETRY_PATH = MESHES_PATH + "\\standard_map_mesh.obj";

    public static Mesh3D loadMeshFromFile(String fileName) {
        final List<String> fileContent = IOFile.getFileContent(fileName);

        final Mesh3DBuilder mesh3DBuilder = new Mesh3DBuilder();

        for(String fileLine: fileContent) {
            final List<String> lineParameters = getLineParameters(fileLine);
            if(isValidLineLength(fileLine, lineParameters)) {
                if(isVertex(lineParameters)) {
                    final Vector3 vertex = parseVertex(lineParameters);
                    mesh3DBuilder.addVertex(vertex);
                }
                else if(isTriangle(lineParameters)) {
                    final Vector3Int vertexReferences = parseTriangle(lineParameters);
                    mesh3DBuilder.addTriangle(vertexReferences.x, vertexReferences.y, vertexReferences.z);
                }
            }
        }

        return mesh3DBuilder.build();
    }

    private static List<String> getLineParameters(final String fileLine) {
        return Arrays.asList(fileLine.split(" "));
    }

    private static boolean isValidLineLength(final String fileLine, final List<String> lineParameters) {
        return fileLine.length() >= 7
                && lineParameters.size() == 4;
    }

    private static boolean isVertex(final List<String> lineParameters) {
        return lineParameters.get(0).equals("v") || lineParameters.get(0).equals("V");
    }

    private static boolean isTriangle(final List<String> lineParameters) {
        return lineParameters.get(0).equals("v") || lineParameters.get(0).equals("V");
    }

    private static Vector3 parseVertex(final List<String> lineParameters) {
        final double positionX = Double.valueOf(lineParameters.get(1));
        final double positionY = Double.valueOf(lineParameters.get(2));
        final double positionZ = Double.valueOf(lineParameters.get(3));

        return new Vector3(positionX, positionY, positionZ);
    }

    private static Vector3Int parseTriangle(final List<String> lineParameters) {
        final int vertexReference0 = Integer.valueOf(lineParameters.get(1));
        final int vertexReference1 = Integer.valueOf(lineParameters.get(2));
        final int vertexReference2 = Integer.valueOf(lineParameters.get(3));

        return new Vector3Int(vertexReference0, vertexReference1, vertexReference2);
    }
}
