package Controller;

import Utils.FileStructure;
import org.json.JSONObject;

public class SettingsController {
    private final String FILENAME = "config/settings.json";
    private final JSONController SETTINGS = new JSONController(FILENAME);

    public SettingsController() {
        if (!FileStructure.fileExists(FILENAME)) {
            writeDefault();
        }
    }

    public void edit(String key, boolean value) {
        JSONObject object = new JSONObject(FileStructure.readFile(FILENAME));
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
