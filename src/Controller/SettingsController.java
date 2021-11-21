package Controller;

import Utils.FileStructure;
import Utils.Json;
import org.json.JSONObject;

public class SettingsController {
    private final String FILENAME = "config/settings.json";
    private final Json SETTINGS = new Json(FILENAME);

    public void edit(String key, boolean value) {
        JSONObject object = new JSONObject(FileStructure.readFile(FILENAME));
        object.put(key, value);

        //Write File
        SETTINGS.write(object);
    }

    public boolean getDisplayMode() {
        return SETTINGS.getBoolValue("darkmode");
    }

    public boolean getDisplayRecords() {
        return SETTINGS.getBoolValue("ShowEmptyRecords");
    }
}
