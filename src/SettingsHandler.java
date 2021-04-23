import org.json.JSONObject;

public class SettingsHandler {
    private final JSONHandler SETTINGS = new JSONHandler("settings.json");

    SettingsHandler() {
        if (!SETTINGS.fileExists()) {
            writeDefaultSettings();
        }
    }

    public void editSettingsJSON(String key, boolean value) {
        String content = SETTINGS.readFile();

        JSONObject object = new JSONObject(content);
        object.put(key, value);
        //Write File
        SETTINGS.write(object);
    }

    private void writeDefaultSettings() {
        //Create Json Object
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("darkmode", false);
        jsonObj.put("language", "eng");
        jsonObj.put("ShowEmptyRecords", false);

        SETTINGS.write(jsonObj);
    }
}
