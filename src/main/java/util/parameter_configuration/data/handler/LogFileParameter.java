package util.parameter_configuration.data.handler;

import util.parameter_configuration.IOFile;

import java.io.File;
import java.util.List;

public class LogFileParameter implements DataHandler {

    private String originalRootedFileName;
    private String rootedFileName;
    private StringBuilder fileNameWithoutExtension;
    private String fileExtension;
    private int numberOfTimesFileChanged;
    private double parsedData;
    private int lineNumberOfParameterInFile;
    private List<String> unparsedParameters;

    public LogFileParameter(String rootedFileName, int lineNumberOfParameterInFile) {
        this.originalRootedFileName = rootedFileName;
        this.rootedFileName = rootedFileName;
        this.fileNameWithoutExtension = new StringBuilder();
        String[] fragmentedFileName = rootedFileName.split("\\.");
        if(fragmentedFileName.length < 2) {
            this.fileNameWithoutExtension.append(fragmentedFileName[0]);
            this.fileExtension = "";
        }
        else {
            for(int i = 0; i < fragmentedFileName.length-1; i++) {
                this.fileNameWithoutExtension.append(fragmentedFileName[i]);
            }
            this.fileExtension = fragmentedFileName[fragmentedFileName.length-1];
        }
        this.numberOfTimesFileChanged = 0;
        this.lineNumberOfParameterInFile = lineNumberOfParameterInFile;
        unparsedParameters = IOFile.getFileContent(rootedFileName);
        parsedData = Double.valueOf(unparsedParameters.get(this.lineNumberOfParameterInFile));
    }

    @Override
    public void set(double newData) {
        // update the new value
        parsedData = newData;

        // change the specific parameter that we are interested in
        unparsedParameters = IOFile.getFileContent(rootedFileName);
        unparsedParameters.set(lineNumberOfParameterInFile, String.valueOf(newData));

        // modify the file with the new parameter (create a new file with slightly changed name and new parameter)
        changeSlightlyFileName();
        IOFile.createFileWithContent(rootedFileName, unparsedParameters);

        // update the real active files that the bot uses too
        IOFile.deleteFile(originalRootedFileName);
        IOFile.createFileWithContent(originalRootedFileName, unparsedParameters);
    }

    @Override
    public double get() {
        return parsedData;
    }

    public LogFileParameter createCopyInFolder(String rootedCopyFolderName) {
        File copy = new File(rootedCopyFolderName + "\\" + getNotRootedFileNameFromRootedFileName(rootedFileName));
        String rootedCopyFileName = copy.getPath();

        if(copy.exists()) {
            IOFile.deleteFile(rootedCopyFileName);
        }
        IOFile.createFileWithContent(rootedCopyFileName, unparsedParameters);

        return new LogFileParameter(rootedCopyFileName, lineNumberOfParameterInFile);
    }

    public void resynchronizeWith(LogFileParameter immutableFileParameter) {
        if(immutableFileParameter.originalRootedFileName.equals(this.originalRootedFileName)) {
            if(this.numberOfTimesFileChanged > immutableFileParameter.numberOfTimesFileChanged) {
                immutableFileParameter.rootedFileName = this.rootedFileName;
                immutableFileParameter.numberOfTimesFileChanged = this.numberOfTimesFileChanged;
            }
            else {
                this.rootedFileName = immutableFileParameter.rootedFileName;
                this.numberOfTimesFileChanged = immutableFileParameter.numberOfTimesFileChanged;
            }
        }
    }

    private void changeSlightlyFileName() {
        // add a number at the end of the file so we can know which iteration it's at
        rootedFileName = fileNameWithoutExtension + "_" + numberOfTimesFileChanged + "." + fileExtension;
        numberOfTimesFileChanged++;
    }

    private String getNotRootedFileNameFromRootedFileName(String rootedFileName) {
        String[] fragmentedFileName = rootedFileName.split("\\\\");
        return fragmentedFileName[fragmentedFileName.length-1];
    }
}
