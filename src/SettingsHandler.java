import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class SettingsHandler {
    private final String BASEPATH = System.getProperty("user.dir") + "/";
    private final String SETTINGSPATH = "settings.json";
    private final String HISTORYPATH = "history.json";

    SettingsHandler() {
        if (!checkExistingFiles(SETTINGSPATH)) {
            writeDefaultSettings();
        }
        if (!checkExistingFiles(HISTORYPATH)) {
            writeDefaultHistory();
        }
    }

    private String read(String fileName) {
        File file = new File(BASEPATH + fileName);
        try {
            return FileUtils.readFileToString(file, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void write(JSONObject object, String fileName) {
        try {
            FileWriter newfile = new FileWriter(BASEPATH + fileName);
            newfile.write(object.toString(4));
            newfile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkExistingFiles(String fileName) {
        File file = new File(BASEPATH + fileName);
        return file.exists();
    }

    public boolean getJSONValue(String key, String fileName) {
        JSONObject readObj = new JSONObject(Objects.requireNonNull(read(fileName)));
        return readObj.getBoolean(key);
    }

    public void editSettingsJSON(String key, boolean value) {
        String content = read(SETTINGSPATH);

        JSONObject object = new JSONObject(content);
        object.put(key, value);
        //Write File
        write(object, SETTINGSPATH);
    }

    private void writeDefaultSettings() {
        //Create Json Object
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("darkmode", false);
        jsonObj.put("language", "eng");
        jsonObj.put("ShowEmptyRecords", false);

        write(jsonObj, "settings.json");
    }

    private void writeDefaultHistory() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("domains", new String[]{});
        write(jsonObj, "history.json");
    }

    public String[] readHistory() {
        JSONObject obj = new JSONObject(Objects.requireNonNull(read("history.json")));

        JSONArray domainList = obj.getJSONArray("domains");
        String[] history = new String[domainList.length()];
        for (int i = 0; i < domainList.length(); i++) {
            history[i] = domainList.getString(i);
        }
        return history;
    }

    public void removeHistoryIndex(int index) {
        JSONObject obj = new JSONObject(Objects.requireNonNull(read("history.json")));
        JSONArray domainList = obj.getJSONArray("domains");
        domainList.remove(index);
        write(obj, "history.json");
    }

    public void addDomainToHistory(String domainName) {
        if (!domainName.equals("")) {
            String content = read(HISTORYPATH);

            JSONObject object = new JSONObject(content);
            JSONArray domainList = object.getJSONArray("domains");
            domainList.put(domainName);

            //Write File
            write(object, HISTORYPATH);
        }
    }
}
