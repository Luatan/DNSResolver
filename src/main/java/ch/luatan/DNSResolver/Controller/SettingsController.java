package ch.luatan.DNSResolver.Controller;

import ch.luatan.DNSResolver.Model.Utils.AppConfig;
import ch.luatan.DNSResolver.Model.Utils.JsonAdapter;
import ch.luatan.DNSResolver.Model.Utils.Config;
import ch.luatan.DNSResolver.Model.Utils.FileHelper;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

public class SettingsController extends JsonAdapter {
    public AppConfig config;

    public void write() {
        write(config, Config.SETTINGS_CONF_FILE);
    }

    protected void load() {
        try {
            Reader reader = FileHelper.getReader(Config.SETTINGS_CONF_FILE);
            config = HANDLER.fromJson(reader, AppConfig.class);
            reader.close();
        } catch (IOException | JsonSyntaxException e) {
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
