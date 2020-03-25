package util.parameter_configuration;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner; // Import the Scanner class to read text files


public class FileReader {

    public static final String LOCAL_CLASS_PATH = "src\\main\\java\\util\\parameter_configuration\\";

    public static List<String> fileContent(String fileName) {
        List<String> fileContent = new ArrayList<>();

        try {
            File file = new File(fileName);
            Scanner myReader = new Scanner(file);
            while (myReader.hasNextLine()) {
                fileContent.add(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return fileContent;
    }
}
