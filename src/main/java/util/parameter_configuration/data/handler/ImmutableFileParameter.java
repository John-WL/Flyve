package util.parameter_configuration.data.handler;

import util.parameter_configuration.IOFile;

import java.io.File;
import java.util.List;

public class ImmutableFileParameter implements DataHandler {

    private String originalRootedFileName;
    private String rootedFileName;
    private String fileNameWithoutExtension;
    private String fileExtension;
    private int numberOfTimeFileChanged;
    private double parsedData;
    private int lineNumberOfParameterInFile;
    private List<String> unparsedParameters;

    public ImmutableFileParameter(String rootedFileName, int lineNumberOfParameterInFile) {
        this.originalRootedFileName = rootedFileName;
        this.rootedFileName = rootedFileName;
        String[] fragmentedFileName = rootedFileName.split("\\.");
        if(fragmentedFileName.length < 2) {
            this.fileNameWithoutExtension = fragmentedFileName[0];
            this.fileExtension = "";
        }
        else {
            for(int i = 0; i < fragmentedFileName.length-1; i++) {
                this.fileNameWithoutExtension += fragmentedFileName[i];
            }
            this.fileExtension = fragmentedFileName[fragmentedFileName.length-1];
        }
        this.numberOfTimeFileChanged = 0;
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
        System.out.println("Watch out! New file created! You should check it out.");
    }

    @Override
    public double get() {
        return parsedData;
    }

    public DataHandler createCopyInFolder(String rootedCopyFolderName) {
        File copy = new File(rootedCopyFolderName + "\\" + getNotRootedFileNameFromRootedFileName(rootedFileName));
        String rootedCopyFileName = copy.getName();

        if(copy.exists()) {
            IOFile.deleteFile(rootedCopyFileName);
        }
        IOFile.createFileWithContent(rootedCopyFileName, unparsedParameters);

        return new ImmutableFileParameter(rootedCopyFileName, lineNumberOfParameterInFile);
    }

    public void resynchronizeWith(ImmutableFileParameter immutableFileParameter) {
        if(immutableFileParameter.originalRootedFileName.equals(this.originalRootedFileName)) {
            if(this.numberOfTimeFileChanged > immutableFileParameter.numberOfTimeFileChanged) {
                immutableFileParameter.rootedFileName = this.rootedFileName;
            }
            else {
                this.rootedFileName = immutableFileParameter.rootedFileName;
            }
        }
    }

    private void changeSlightlyFileName() {
        // add a number at the end of the file so we can know which iteration it's at
        rootedFileName = fileNameWithoutExtension + numberOfTimeFileChanged + fileExtension;
        numberOfTimeFileChanged++;
    }

    private String getNotRootedFileNameFromRootedFileName(String rootedFileName) {
        String[] fragmentedFileName = rootedFileName.split("\\\\");
        return fragmentedFileName[fragmentedFileName.length-1];
    }
}
