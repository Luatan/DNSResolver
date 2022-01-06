package Model.Utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileStructure {

    public static final String DIR_HOME = System.getProperty("user.dir") + "/";

    public static Reader getReader(String filename) throws IOException {
        return Files.newBufferedReader(Paths.get(filename));
    }

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

    public static boolean createFileFromPath(String ressourcePath, String destPath) {
        if (FileStructure.fileExists(destPath)) {
            return true;
        }

        // try to get Stream
        try (InputStream is = FileStructure.class.getClassLoader().getResourceAsStream(ressourcePath)) {
            // create parent dirs
            File file = new File(FileStructure.DIR_HOME + destPath);
            file.getParentFile().mkdirs();
            // convert input stream to file
            FileUtils.copyInputStreamToFile(is, file);
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean createFile(String fileContent, String destPath) {
        String path = FileStructure.DIR_HOME + destPath;

        try {
            // create Parent dirs
            File file = new File(path);
            file.getParentFile().mkdirs();
            FileWriterWithEncoding fileWriter = new FileWriterWithEncoding(path, "utf-8");
            fileWriter.write(fileContent);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
