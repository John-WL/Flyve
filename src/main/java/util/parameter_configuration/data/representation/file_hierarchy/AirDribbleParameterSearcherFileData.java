package util.parameter_configuration.data.representation.file_hierarchy;

import util.parameter_configuration.data.handler.LogFileParameter;
import util.parameter_configuration.data.representation.DataRepresentation;

import java.util.ArrayList;
import java.util.List;

public class AirDribbleParameterSearcherFileData extends DataRepresentation<LogFileParameter> {


    private final static String ROOT = "src\\main\\java\\parameter_search\\air_dribbling";
    private final static String SERIALIZED_PARAMETER_ROOT = ROOT + "\\serialized_parameters";
    private final static String RAW_DATA_ROOT = SERIALIZED_PARAMETER_ROOT + "\\raw_data";
    private final static String INITIAL_DATA_ROOT = SERIALIZED_PARAMETER_ROOT + "\\initial_data";
    private final static String FINAL_DATA_ROOT = SERIALIZED_PARAMETER_ROOT + "\\final_data";
    private final static String PLAYER_DESTINATION_ON_BALL_FILE_NAME = "player_destination_on_ball.pcg";
    private final static String PLAYER_DISPLACEMENT_AMOUNT_COEFFICIENT_FILE_NAME = "player_displacement_amount_coefficient.arb";
    private final static String PLAYER_ORIENTATION_XY_FILE_NAME = "player_orientation_xy.pcg";
    private final static String PLAYER_ORIENTATION_Z_FILE_NAME = "player_orientation_z.pcg";
    private final static int NUMBER_OF_PARAMETER = 7;
    private final static int NUMBER_OF_FILES = 4;
    private final static int[] LINE_NUMBER_OF_PARAMETERS = {0, 2, 0, 0, 2, 0, 2};
    private final static int[] INDEX_OF_FIRST_INSTANCE_OF_FILE_NAMES = {0, 2, 3, 5};
    private final static String[] ROOTED_FILE_NAME_OF_INITIAL_PARAMETERS = {
            INITIAL_DATA_ROOT + "\\" + PLAYER_DESTINATION_ON_BALL_FILE_NAME,
            INITIAL_DATA_ROOT + "\\" + PLAYER_DESTINATION_ON_BALL_FILE_NAME,
            INITIAL_DATA_ROOT + "\\" + PLAYER_DISPLACEMENT_AMOUNT_COEFFICIENT_FILE_NAME,
            INITIAL_DATA_ROOT + "\\" + PLAYER_ORIENTATION_XY_FILE_NAME,
            INITIAL_DATA_ROOT + "\\" + PLAYER_ORIENTATION_XY_FILE_NAME,
            INITIAL_DATA_ROOT + "\\" + PLAYER_ORIENTATION_Z_FILE_NAME,
            INITIAL_DATA_ROOT + "\\" + PLAYER_ORIENTATION_Z_FILE_NAME
    };
    private final static String[] ROOTED_FINAL_FILE_NAMES = {
            FINAL_DATA_ROOT + "\\" + PLAYER_DESTINATION_ON_BALL_FILE_NAME,
            FINAL_DATA_ROOT + "\\" + PLAYER_DISPLACEMENT_AMOUNT_COEFFICIENT_FILE_NAME,
            FINAL_DATA_ROOT + "\\" + PLAYER_ORIENTATION_XY_FILE_NAME,
            FINAL_DATA_ROOT + "\\" + PLAYER_ORIENTATION_Z_FILE_NAME
    };

    public final static String AIR_DRIBBLE_FILENAME = RAW_DATA_ROOT + "\\" + PLAYER_DESTINATION_ON_BALL_FILE_NAME;
    public final static String AIR_DRIBBLE_DISPLACEMENT_AMOUNT_COEFFICIENT = INITIAL_DATA_ROOT + "\\" + PLAYER_DISPLACEMENT_AMOUNT_COEFFICIENT_FILE_NAME;
    public final static String AIR_DRIBBLE_ORIENTATION_XY_FILENAME = INITIAL_DATA_ROOT + "\\" + PLAYER_ORIENTATION_XY_FILE_NAME;
    public final static String AIR_DRIBBLE_ORIENTATION_Z_FILENAME = INITIAL_DATA_ROOT + "\\" + PLAYER_ORIENTATION_Z_FILE_NAME;


    private static List<LogFileParameter> immutableFileParameters;

    public AirDribbleParameterSearcherFileData() {
        super(generateDataHandlerList());
    }

    public void isolateBestResultsInFinalDataFolder() {
        // take the current Immutable File Parameters, and put them in the final data folder
        for(int i = 0; i < NUMBER_OF_FILES; i++) {
            getDataHandlerList().get(INDEX_OF_FIRST_INSTANCE_OF_FILE_NAMES[i]).createCopyInFolder(ROOTED_FINAL_FILE_NAMES[i]);
        }
    }

    public void resynchronizeParameters() {
        immutableFileParameters.get(0).resynchronizeWith(immutableFileParameters.get(1));
        immutableFileParameters.get(3).resynchronizeWith(immutableFileParameters.get(4));
        immutableFileParameters.get(5).resynchronizeWith(immutableFileParameters.get(6));
    }

    private static List<LogFileParameter> generateDataHandlerList() {
        immutableFileParameters = new ArrayList<>();

        // make sure the file tree has everything it needs
        try {
            inspectFileHierarchy();
        }
        catch(UnexpectedFileHierarchy e) {
            e.printStackTrace();
        }

        // copy the file parameters from the initial values in the initial folder
        for(int i = 0; i < NUMBER_OF_PARAMETER; i++) {
            immutableFileParameters.add(new LogFileParameter(ROOTED_FILE_NAME_OF_INITIAL_PARAMETERS[i], LINE_NUMBER_OF_PARAMETERS[i])
                    .createCopyInFolder(RAW_DATA_ROOT)
            );
        }

        return immutableFileParameters;
    }

    private static void inspectFileHierarchy() throws UnexpectedFileHierarchy {
        // build expected internal file hierarchy composition.
        // if some folders or some files (some files?) are missing, then throw.
        ExpectedFileHierarchy expectedFileTree = new ExpectedFileHierarchy(ROOT);
        expectedFileTree.withFolder(new ExpectedFileHierarchy(SERIALIZED_PARAMETER_ROOT)
                        .withFolder(new ExpectedFileHierarchy(INITIAL_DATA_ROOT)
                                .withFile(PLAYER_DESTINATION_ON_BALL_FILE_NAME)
                                .withFile(PLAYER_DISPLACEMENT_AMOUNT_COEFFICIENT_FILE_NAME)
                                .withFile(PLAYER_ORIENTATION_XY_FILE_NAME)
                                .withFile(PLAYER_ORIENTATION_Z_FILE_NAME))
                        .withFolder(new ExpectedFileHierarchy(RAW_DATA_ROOT))
                        .withFolder(new ExpectedFileHierarchy(FINAL_DATA_ROOT))
                );
        boolean isTreeAsExpected = expectedFileTree.inspect();

        if(!isTreeAsExpected) throw new UnexpectedFileHierarchy(expectedFileTree);
    }
}

class UnexpectedFileHierarchy extends RuntimeException {

    private ExpectedFileHierarchy missingFileHierarchy;

    UnexpectedFileHierarchy(ExpectedFileHierarchy expectedFileTree) {
        this.missingFileHierarchy = expectedFileTree;
    }

    @Override
    public void printStackTrace() {
        System.out.println("expected file tree is:\n" + missingFileHierarchy + "\n\n" + "Missing elements are:\n" + missingFileHierarchy.getMissingFileHierarchy());
    }

}
