package Utils;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.io.File;
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

    public static void createFile(String ressourcePath, String destPath) {
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

    public static void copyDirFromRessources(String srcPath, String destPath) {
        File test;
        try {
            test = new File(FileStructure.class.getClassLoader().getResource(srcPath).getFile());
        } catch (NullPointerException e){
            e.printStackTrace();
            return;
        }

        try {
            FileUtils.copyDirectoryToDirectory(test, new File(FileStructure.DIR_HOME + destPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
