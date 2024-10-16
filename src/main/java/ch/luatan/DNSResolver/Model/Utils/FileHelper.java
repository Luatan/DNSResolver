package ch.luatan.DNSResolver.Model.Utils;

import ch.luatan.DNSResolver.DNSResolver;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileHelper {

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
        DNSResolver.LOGGER.debug("reading file " + file.getAbsolutePath());
        try {
            return FileUtils.readFileToString(file, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean createFileFromPath(String ressourcePath, String destPath) {
        if (FileHelper.fileExists(destPath)) {
            return true;
        }

        // try to get Stream
        try (InputStream is = FileHelper.class.getClassLoader().getResourceAsStream(ressourcePath)) {
            // create parent dirs
            File file = new File(FileHelper.DIR_HOME + destPath);
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
        String path = FileHelper.DIR_HOME + destPath;
        DNSResolver.LOGGER.debug("Create File at " + new File(path).getAbsolutePath());

        try {
            // create Parent dirs
            File file = new File(path);
            file.getParentFile().mkdirs();
            FileWriter fileWriter = new FileWriter(path);
            fileWriter.write(fileContent);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            DNSResolver.LOGGER.error(e.getMessage());
            return false;
        }
        return true;
    }
}
