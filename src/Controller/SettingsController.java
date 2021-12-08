package Controller;

import Model.AppConfig;
import Model.JsonAdapter;
import Utils.Config;
import Utils.FileStructure;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.output.FileWriterWithEncoding;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SettingsController extends JsonAdapter {
    public AppConfig config;

    SettingsController() {
        super(AppConfig.class);
    }

    public void write() {
        try {
            FileWriterWithEncoding file = new FileWriterWithEncoding(FileStructure.DIR_HOME + Config.SETTINGS_CONF_FILE, "utf-8");
            file.write(HANDLER.toJson(config));
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void load() {
        try {
            Reader reader = Files.newBufferedReader(Paths.get(Config.SETTINGS_CONF_FILE));
            config = (AppConfig) HANDLER.fromJson(reader, type);
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

    protected void reset() {
        System.err.println("Settings File error.... reseting File");
        File file = new File(Config.SETTINGS_CONF_FILE);
        if (file.delete()) {
            System.out.println("true");
        } else {
            System.out.println("false");
        }

        Config.createSettingsConfig();
        load();
    }
}
