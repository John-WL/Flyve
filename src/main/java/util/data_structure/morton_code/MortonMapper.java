package util.data_structure.morton_code;

import util.math.vector.Vector3;
import util.math.vector.Vector3Int;
import util.shapes.Triangle3D;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MortonMapper {

    public static Map<Long, Triangle3D> map(List<Triangle3D> triangleList) {
        final Map<Long, Triangle3D> map = new HashMap<>();

        triangleList
                .forEach(t -> {
                    Vector3 v = t.centerPoint;
                    // Here, we shift the 3d position and scale it.
                    // This is to ensure positive positions when querying the position of triangles in the real rocket league map.
                    // The scaling by 100 is arbitrary, and is there to ensure that we have enough precision so that no 2 triangles have the same id.
                    // With this kind of scaling, we cannot have vectors bigger than ~7280 on each axis, which is fine
                    // for our use case (we are looking for max ~6000, so we have a bit of play here, which is great).
                    int x = (int)((v.x + 10000) * 100);
                    int y = (int)((v.y + 10000) * 100);
                    int z = (int)((v.z + 10000) * 100);
                    map.put(MortonEncoder3D.encode(x, y, z), t);
        });

        return map;
    }

}
