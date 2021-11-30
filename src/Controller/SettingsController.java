package Controller;

import Model.AppConfig;
import Utils.Config;
import Utils.FileStructure;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.output.FileWriterWithEncoding;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SettingsController {
    public Gson jsonHandler = new GsonBuilder().setPrettyPrinting().create();
    private final String FILENAME = "config/settings.json";
    public AppConfig config;

    SettingsController() {
        load();
    }

    public void write() {
        try {
            FileWriterWithEncoding file = new FileWriterWithEncoding(FileStructure.DIR_HOME + FILENAME, "utf-8");
            file.write(jsonHandler.toJson(config));
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void load() {
        try {
            Reader reader = Files.newBufferedReader(Paths.get(FILENAME));
            config = jsonHandler.fromJson(reader, AppConfig.class);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException jsonE) {
            reset();
        }
        if (config == null) {
            reset();
        }
    }

    private void reset() {
        System.err.println("Settings File error.... reseting File");
        File file = new File(FILENAME);
        if (file.delete()) {
            System.out.println("true");
        } else {
            System.out.println("false");
        }

        Config.createSettingsConfig();
        load();
    }
}
