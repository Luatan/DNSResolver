package com.dns;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SettingsHandler {
    private boolean emptyRecords;
    private boolean darkmode;
    private final String SETTINGSPATH = System.getProperty("user.dir") + "/settings.json";

    SettingsHandler() {
        if (!checkExistingFiles()) {
            writeDefaultSettings();
        }
        readJSONSettings();
    }

    private void writeDefaultSettings() {
        //Create Json Object
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("darkmode", false);
        jsonObj.put("language", "eng");
        jsonObj.put("ShowEmptyRecords", false);

        write(jsonObj);
    }

    private void readJSONSettings(){
        String content = read();

        JSONObject readObj = new JSONObject(content);

        darkmode = readObj.getBoolean("darkmode");
        emptyRecords = readObj.getBoolean("ShowEmptyRecords");
    }

    private void changeJSONFile(JSONObject object) {
        object.put("darkmode", darkmode);
        object.put("ShowEmptyRecords", emptyRecords);
        write(object);
    }

    public void changeValueJSON(String key, boolean value){
        String content = read();

        JSONObject object = new JSONObject(content);
        object.put(key, value);
        //Write File
        write(object);

    }

    private void write(JSONObject object){
        try {
            FileWriter newfile = new FileWriter(SETTINGSPATH);
            newfile.write(object.toString(4));
            newfile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String read(){
        File file = new File(SETTINGSPATH);
        try {
            return FileUtils.readFileToString(file, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private boolean checkExistingFiles(){
        File file = new File(SETTINGSPATH);
        return file.exists();
    }

    public boolean getDarkmode() {
        return this.darkmode;
    }

    public boolean getEmptyRecords() {
        return this.emptyRecords;
    }
}
