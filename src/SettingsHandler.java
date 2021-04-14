import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class SettingsHandler {
    private final JSONHandler SETTINGS = new JSONHandler("settings.json");
    private final JSONHandler HISTORY = new JSONHandler("history.json");

    SettingsHandler() {
        if (!SETTINGS.fileExists()) {
            writeDefaultSettings();
        }
        if (!HISTORY.fileExists()) {
            writeDefaultHistory();
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

    private void writeDefaultHistory() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("domains", new String[]{});
        HISTORY.write(jsonObj);
    }

    public String[] readHistory() {
        JSONObject obj = new JSONObject(HISTORY.readFile());

        JSONArray domainList = obj.getJSONArray("domains");
        String[] history = new String[domainList.length()];
        for (int i = 0; i < domainList.length(); i++) {
            history[i] = domainList.getString(i);
        }
        return history;
    }

    public void removeHistoryIndex(int index) {
        JSONObject obj = new JSONObject(HISTORY.readFile());
        JSONArray domainList = obj.getJSONArray("domains");
        domainList.remove(index);
        HISTORY.write(obj);
    }

    public void addDomainToHistory(String domainName) {
        if (!domainName.equals("")) {
            String content = HISTORY.readFile();

            JSONObject object = new JSONObject(content);
            JSONArray domainList = object.getJSONArray("domains");
            domainList.put(domainName);

            //Write File
            HISTORY.write(object);
        }
    }
}
