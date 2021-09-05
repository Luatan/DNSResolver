package Controller;

import Utils.Files;
import org.json.JSONObject;

public class SettingsController {
    private final String FILENAME = "settings.json";
    private final JSONController SETTINGS = new JSONController(FILENAME);

    public SettingsController() {
        if (!Files.fileExists(FILENAME)) {
            writeDefault();
        }
    }

    public void edit(String key, boolean value) {
        JSONObject object = new JSONObject(Files.readFile(FILENAME));
        object.put(key, value);

        //Write File
        SETTINGS.write(object);
    }

    private void writeDefault() {
        //Create Json Object
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("darkmode", false);
        jsonObj.put("language", "eng");
        jsonObj.put("ShowEmptyRecords", false);

        SETTINGS.write(jsonObj);
    }

    public boolean getDisplayMode() {
        return SETTINGS.getBoolValue("darkmode");
    }

    public boolean getDisplayRecords() {
        return SETTINGS.getBoolValue("ShowEmptyRecords");
    }
}
