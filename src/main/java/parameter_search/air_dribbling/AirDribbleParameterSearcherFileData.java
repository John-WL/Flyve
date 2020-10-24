package parameter_search.air_dribbling;

import util.machine_learning_models.generic_data_structure.generic_data.FileParameter;
import util.machine_learning_models.generic_data_structure.generic_data.LogFileParameter;
import util.machine_learning_models.generic_data_structure.generic_data.SafeLineIncrementFileParameter;
import util.machine_learning_models.generic_data_structure.list.GenericDataList;
import util.file_hierarchy_inspector.ExpectedFileHierarchy;
import util.file_hierarchy_inspector.UnexpectedFileHierarchyException;

import java.util.ArrayList;
import java.util.List;

public class AirDribbleParameterSearcherFileData extends GenericDataList<FileParameter> {

    private final static String ROOT = "src\\main\\java\\parameter_search\\air_dribbling";
    private final static String SERIALIZED_PARAMETER_ROOT = ROOT + "\\serialized_parameters";
    private final static String BOT_EVALUATION_RESULTS_ROOT = ROOT + "\\bot_evaluation_results";
    private final static String RAW_DATA_ROOT = SERIALIZED_PARAMETER_ROOT + "\\raw_data";
    private final static String INITIAL_DATA_ROOT = SERIALIZED_PARAMETER_ROOT + "\\initial_data";
    private final static String FINAL_DATA_ROOT = SERIALIZED_PARAMETER_ROOT + "\\final_data";

    private final static String BOT_EVALUATION_RESULTS_FILE_NAME = "air_dribbling_evaluations.rsl";
    private final static String PLAYER_DESTINATION_ON_BALL_FILE_NAME = "player_destination_on_ball.pcg";
    private final static String PLAYER_DISPLACEMENT_AMOUNT_COEFFICIENT_FILE_NAME = "player_displacement_amount_coefficient.arb";
    private final static String PLAYER_ORIENTATION_XY_FILE_NAME = "player_orientation_xy.pcg";
    private final static String PLAYER_ORIENTATION_Z_FILE_NAME = "player_orientation_z.pcg";
    private final static String BALL_MAXIMUM_OFFSET_FILE_NAME = "ball_maximum_offset.arb";
    private final static String AERIAL_BOOST_FILE_NAME = "aerial_boost_amount.pcg";

    private final static int[] LINE_NUMBER_OF_PARAMETERS = {0, 2, 0, 0, 2, 0, 2, 0, 0, 2};
    private final static int[] INDEX_OF_FIRST_INSTANCE_OF_FILE_NAMES = {0, 2, 3, 5, 7, 8};
    private final static String[] ROOTED_FILE_NAME_OF_INITIAL_PARAMETERS = {
            INITIAL_DATA_ROOT + "\\" + PLAYER_DESTINATION_ON_BALL_FILE_NAME,
            INITIAL_DATA_ROOT + "\\" + PLAYER_DESTINATION_ON_BALL_FILE_NAME,
            INITIAL_DATA_ROOT + "\\" + PLAYER_DISPLACEMENT_AMOUNT_COEFFICIENT_FILE_NAME,
            INITIAL_DATA_ROOT + "\\" + PLAYER_ORIENTATION_XY_FILE_NAME,
            INITIAL_DATA_ROOT + "\\" + PLAYER_ORIENTATION_XY_FILE_NAME,
            INITIAL_DATA_ROOT + "\\" + PLAYER_ORIENTATION_Z_FILE_NAME,
            INITIAL_DATA_ROOT + "\\" + PLAYER_ORIENTATION_Z_FILE_NAME,
            INITIAL_DATA_ROOT + "\\" + BALL_MAXIMUM_OFFSET_FILE_NAME,
            INITIAL_DATA_ROOT + "\\" + AERIAL_BOOST_FILE_NAME,
            INITIAL_DATA_ROOT + "\\" + AERIAL_BOOST_FILE_NAME,
    };
    private final static String[] ROOTED_FINAL_FOLDER_NAMES = {
            FINAL_DATA_ROOT,
            FINAL_DATA_ROOT,
            FINAL_DATA_ROOT,
            FINAL_DATA_ROOT,
            FINAL_DATA_ROOT,
            FINAL_DATA_ROOT,
    };

    public final static String AIR_DRIBBLE_ROOTED_FILENAME = RAW_DATA_ROOT + "\\" + PLAYER_DESTINATION_ON_BALL_FILE_NAME;
    public final static String AIR_DRIBBLE_DISPLACEMENT_AMOUNT_COEFFICIENT_ROOTED_FILENAME = RAW_DATA_ROOT + "\\" + PLAYER_DISPLACEMENT_AMOUNT_COEFFICIENT_FILE_NAME;
    public final static String AIR_DRIBBLE_MAXIMUM_BALL_OFFSET_ROOTED_FILENAME = RAW_DATA_ROOT + "\\" + BALL_MAXIMUM_OFFSET_FILE_NAME;
    public final static String AERIAL_BOOST_ROOTED_FILENAME = RAW_DATA_ROOT + "\\" + AERIAL_BOOST_FILE_NAME;


    public final static String AIR_DRIBBLE_ORIENTATION_XY_ROOTED_FILENAME = RAW_DATA_ROOT + "\\" + PLAYER_ORIENTATION_XY_FILE_NAME;
    public final static String AIR_DRIBBLE_ORIENTATION_Z_ROOTED_FILENAME = RAW_DATA_ROOT + "\\" + PLAYER_ORIENTATION_Z_FILE_NAME;

    public final static String BOT_EVALUATION_ROOTED_FILENAME = BOT_EVALUATION_RESULTS_ROOT + "\\" + BOT_EVALUATION_RESULTS_FILE_NAME;


    private static List<FileParameter> fileParameters;
    private static FileParameter airDribbleEvaluatorLogger;

    public AirDribbleParameterSearcherFileData() {
        super(generateDataHandlerList());
        linkFiles();
    }

    public void isolateBestResultsInFinalDataFolder() {
        // take the current File Parameters, and put them in the final data folder
        for(int i = 0; i < ROOTED_FINAL_FOLDER_NAMES.length; i++) {
            getDataHandlerList().get(INDEX_OF_FIRST_INSTANCE_OF_FILE_NAMES[i]).createCopyInFolder(ROOTED_FINAL_FOLDER_NAMES[i]);
        }
    }

    public void linkFiles() {
        fileParameters.get(0).linkWith(fileParameters.get(1));
        fileParameters.get(1).linkWith(fileParameters.get(0));
        fileParameters.get(3).linkWith(fileParameters.get(4));
        fileParameters.get(4).linkWith(fileParameters.get(3));
        fileParameters.get(5).linkWith(fileParameters.get(6));
        fileParameters.get(6).linkWith(fileParameters.get(5));
        fileParameters.get(8).linkWith(fileParameters.get(9));
        fileParameters.get(9).linkWith(fileParameters.get(8));
    }

    public FileParameter getAirDribbleEvaluatorFileParameter() {
        return airDribbleEvaluatorLogger;
    }

    private static List<FileParameter> generateDataHandlerList() {
        fileParameters = new ArrayList<>();
        // make sure the file tree has everything it needs
        try {
            inspectFileHierarchy();
        }
        catch(UnexpectedFileHierarchyException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

        // copy the file parameters from the initial values in the initial folder
        for(int i = 0; i < ROOTED_FILE_NAME_OF_INITIAL_PARAMETERS.length; i++) {
            fileParameters.add(new LogFileParameter(ROOTED_FILE_NAME_OF_INITIAL_PARAMETERS[i], LINE_NUMBER_OF_PARAMETERS[i])
                    .createCopyInFolder(RAW_DATA_ROOT)
            );
        }

        airDribbleEvaluatorLogger = new SafeLineIncrementFileParameter(BOT_EVALUATION_ROOTED_FILENAME);

        return fileParameters;
    }

    private static void inspectFileHierarchy() throws UnexpectedFileHierarchyException {
        // build expected internal file hierarchy composition.
        // if some folders or some files (some files?) are missing, then throw.
        ExpectedFileHierarchy expectedFileTree = new ExpectedFileHierarchy(ROOT);
        expectedFileTree.withFolder(new ExpectedFileHierarchy(SERIALIZED_PARAMETER_ROOT)
                        .withFolder(new ExpectedFileHierarchy(INITIAL_DATA_ROOT)
                                .withFile(PLAYER_DESTINATION_ON_BALL_FILE_NAME)
                                .withFile(PLAYER_DISPLACEMENT_AMOUNT_COEFFICIENT_FILE_NAME)
                                .withFile(PLAYER_ORIENTATION_XY_FILE_NAME)
                                .withFile(PLAYER_ORIENTATION_Z_FILE_NAME)
                                .withFile(BALL_MAXIMUM_OFFSET_FILE_NAME)
                                .withFile(AERIAL_BOOST_FILE_NAME))
                        .withFolder(new ExpectedFileHierarchy(RAW_DATA_ROOT))
                        .withFolder(new ExpectedFileHierarchy(FINAL_DATA_ROOT))
                )
        .withFolder(new ExpectedFileHierarchy(BOT_EVALUATION_RESULTS_ROOT)
                .withFile(BOT_EVALUATION_RESULTS_FILE_NAME)
        );
        boolean isTreeAsExpected = expectedFileTree.inspect();

        if(!isTreeAsExpected) throw new UnexpectedFileHierarchyException(expectedFileTree);
    }
}
