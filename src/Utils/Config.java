package Utils;

import org.json.JSONObject;

import java.io.File;
import java.util.Objects;

public class Config {
    public static boolean CACHING = true;
    public static int CACHE_TIME_TO_LIVE = 20;
    public static final String CACHE_FILES = "cache/";
    public static final String WHOIS_CONF_FILE = "config/whois_servers.json";
    public static final String SETTINGS_CONF_FILE = "config/settings.json";
    public static final String HISTORY_LOG_FILE = "logs/history.json";
    public static final String IP_API_LOG_FILE = "logs/IP_API_req.json";

    public static void createWhoisConfig() {
        if (!FileStructure.fileExists(WHOIS_CONF_FILE)){
            FileStructure.createFileFromPath("config/default_whois.json", WHOIS_CONF_FILE);
        }
    }

    public static void createHistoryConfig(){
        if (!FileStructure.fileExists(HISTORY_LOG_FILE)) {
            JSONObject obj = new JSONObject().put("domains", new String[]{});
            FileStructure.createFile(obj.toString(4), HISTORY_LOG_FILE);
        }
    }

    public static void createSettingsConfig() {
        if (!FileStructure.fileExists(SETTINGS_CONF_FILE)){
            FileStructure.createFileFromPath("config/default_settings.json", SETTINGS_CONF_FILE);
        }
    }

    public static void cleanCacheFiles() {
        File cacheDir = new File(Config.CACHE_FILES);
        if (!cacheDir.exists()) {
            return;
        }
        try {
            for (String file:Objects.requireNonNull(cacheDir.list())) {
                WhoisCache cache = new WhoisCache(file.substring(0, file.indexOf(".tmp")));
                cache.isValid();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
