package ch.simon.Controller;

import ch.simon.Model.AppConfig;
import ch.simon.Model.JsonAdapter;
import ch.simon.Utils.Config;
import ch.simon.Utils.FileStructure;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

public class SettingsController extends JsonAdapter {
    public AppConfig config;

    SettingsController() {
        super(AppConfig.class);
    }

    public void write() {
        write(config, Config.SETTINGS_CONF_FILE);
    }

    protected void load() {
        try {
            Reader reader = FileStructure.getReader(Config.SETTINGS_CONF_FILE);
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
        System.err.println("Settings File error.... reseting File...");
        File file = new File(Config.SETTINGS_CONF_FILE);
        if (file.delete()) {
            System.err.println(Config.SETTINGS_CONF_FILE + " was deleted!");
        }

        Config.createSettingsConfig();
        load();
    }
}
