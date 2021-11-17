package Utils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileStructure {

    public static final String DIR_HOME = System.getProperty("user.dir") + "/";


    public static boolean fileExists(String filename) {
        File file = new File(DIR_HOME + filename);
        return file.exists();
    }

    public static boolean directoryExists(String directoryName) {
        return Files.isDirectory(Paths.get(DIR_HOME + directoryName));
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

    public static void createDir(String dirPath) {
        if (!FileStructure.directoryExists(dirPath)){
            try {
                Files.createDirectories(Paths.get(FileStructure.DIR_HOME + dirPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createFileFromPath(String ressourcePath, String destPath) {
        if (FileStructure.fileExists(destPath)){
            return;
        }

        // try to get Stream
        try (InputStream is = FileStructure.class.getClassLoader().getResourceAsStream(ressourcePath)) {
            // convert input stream to file
            FileUtils.copyInputStreamToFile(is, new File(FileStructure.DIR_HOME + destPath));

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void createFile(String fileContent, String destPath) {
        if (FileStructure.fileExists(destPath)){
            return;
        }

        try {
            FileWriterWithEncoding file = new FileWriterWithEncoding(FileStructure.DIR_HOME + destPath, "utf-8");
            file.write(fileContent);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
