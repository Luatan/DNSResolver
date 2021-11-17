package Utils;

import org.json.JSONObject;

public class Config {
    public static final String WHOIS_CONF_FILE = "config/whois_servers.json";
    public static final String HISTORY_CONF_FILE = "logs/history.json";
    public static final String SETTINGS_CONF_FILE = "config/settings.json";

    public static void createWhoisConfig() {
        if (!FileStructure.fileExists(WHOIS_CONF_FILE)){
            FileStructure.createFileFromPath("config/default_whois.json", WHOIS_CONF_FILE);
        }
    }

    public static void createHistoryConfig(){
        if (!FileStructure.fileExists(HISTORY_CONF_FILE)) {
            JSONObject obj = new JSONObject();
            obj.put("domains", new String[]{});
            FileStructure.createFile(obj.toString(4), HISTORY_CONF_FILE);
        }
    }

    public static void createSettingsConfig() {
        if (!FileStructure.fileExists(SETTINGS_CONF_FILE)) {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("darkmode", false);
            jsonObj.put("ShowEmptyRecords", false);
            FileStructure.createFile(jsonObj.toString(4), SETTINGS_CONF_FILE);
        }
    }

    public static void createDirs() {
        //Create essential folders in RunDir (on runtime)
        String[] dirs = new String[] {"logs", "config"};
        for (String dir:dirs) {
            FileStructure.createDir(dir);
        }
    }
}
