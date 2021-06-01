package external_data.animation;

import rlbotexample.input.animations.CarGroup;
import rlbotexample.input.animations.IndexedCarGroup;
import rlbotexample.input.animations.CarGroupAnimation;
import util.math.vector.ZyxOrientedPosition;
import util.math.vector.Vector3;
import util.parameter_configuration.IOFile;
import util.parameter_configuration.ObjectSerializer;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * This small program works in coordination with blender.
 * Basically, there is a script that gets the data about object position and orientations,
 * and this algorithm serves as a bridge to transfer the parsed data into a usable object for later use in the program.
 *
 * With this, we can load in rocket league animations that we carefully crafted in blender!
 */
public class CarAnimationImporter {

    public static final String ANIMATIONS_BASE_FOLDER_PATH = "src\\main\\resources\\boss rig";
    public static final String ANIMATIONS_EXTENSION_NAME = ".cop";
    public static final String OBJECT_STREAMING_EXTENSION_NAME = ".sob";

    public static void main(String[] args) {
        IOFile.getFileNamesIn("src\\main\\resources\\boss rig")
                .stream()
                .filter(fileName -> {
                    int extensionIndex = fileName.lastIndexOf('.');
                    String extension = fileName.substring(extensionIndex);
                    return extension.equals(ANIMATIONS_EXTENSION_NAME);
                })
                .map(fileName -> ANIMATIONS_BASE_FOLDER_PATH + "\\" + fileName)
                .forEach(CarAnimationImporter::fileDataToStreamedObject);
    }

    // object streaming of data generated from blender
    private static void fileDataToStreamedObject(String filePath) {
        List<String> fileData = IOFile.getFileContent(filePath);

        AtomicReference<Integer> previousFrameIdRef = new AtomicReference<>(-1);
        AtomicReference<Integer> newFrameIdRef = new AtomicReference<>(0);
        Map<Integer, Integer> idLinks = new HashMap<>();

        List<IndexedCarGroup> carMeshFrames = new ArrayList<>();

        // parse the damn file
        fileData.forEach(s -> {
            // just get the floats in an array
            String[] valuesStr = s.split(":");
            List<Float> valuesDouble = Arrays.stream(valuesStr).map(Float::valueOf).collect(Collectors.toList());

            // establish the data
            int objectBlenderId = valuesDouble.get(0).intValue();
            int frameId = valuesDouble.get(1).intValue();
            double positionX = valuesDouble.get(2).doubleValue();
            double positionY = valuesDouble.get(3).doubleValue();
            double positionZ = valuesDouble.get(4).doubleValue();
            double rotationX = valuesDouble.get(5).doubleValue();
            double rotationY = valuesDouble.get(6).doubleValue();
            double rotationZ = valuesDouble.get(7).doubleValue();

            // update current frame id reference
            newFrameIdRef.set(frameId);

            Integer objectJavaIndex = idLinks.get(objectBlenderId);

            if(objectJavaIndex == null) {
                idLinks.put(objectBlenderId, carMeshFrames.size());
                objectJavaIndex = carMeshFrames.size();
            }

            // if this is a new frame
            if(!previousFrameIdRef.get().equals(newFrameIdRef.get())) {
                previousFrameIdRef.set(frameId);
                carMeshFrames.add(new IndexedCarGroup(objectJavaIndex));
            }

            CarGroup carGroup = carMeshFrames.get(carMeshFrames.size()-1).carGroup;

            carGroup.orientedPositions.add(new ZyxOrientedPosition(
                    new Vector3(positionX, positionY, positionZ),
                    new Vector3(rotationX, rotationY, rotationZ)));
        });

        // output the data in a file for ez loading
        ObjectSerializer.save(new CarGroupAnimation(carMeshFrames), filePath.replaceAll("\\" + ANIMATIONS_EXTENSION_NAME, OBJECT_STREAMING_EXTENSION_NAME));
    }
}
