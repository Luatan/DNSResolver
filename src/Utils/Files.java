package Utils;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.io.File;

public class Files {

    public static final String DIR_HOME = System.getProperty("user.dir") + "/";


    public static boolean fileExists(String filename) {
        File file = new File(DIR_HOME + filename);
        return file.exists();
    }

    public static String readFile(String filename) {
        File file = new java.io.File(DIR_HOME + filename);
        try {
            return FileUtils.readFileToString(file, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
