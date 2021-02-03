package com.dns;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SettingsHandler {
    private boolean emptyRecords;
    private boolean darkmode;

    SettingsHandler() {
        if (!checkExistingFiles()) {
            writeJSONSettingsFile();
        }
        readJSONSettings();
    }

    private boolean checkExistingFiles(){
        File file = new File(System.getProperty("user.dir") + "/settings.json");
        return file.exists();
    }

    private void writeJSONSettingsFile() {
        //Create Json Object
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("darkmode", false);
        jsonObj.put("language", "eng");
        jsonObj.put("ShowEmptyRecords", false);

        try {
            FileWriter file = new FileWriter(System.getProperty("user.dir") + "/settings.json");
            file.write(jsonObj.toString(4));
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("JSON file created: " + jsonObj);
    }

    private void readJSONSettings(){
        File file = new File(System.getProperty("user.dir") + "/settings.json");
        System.out.println(System.getProperty("user.dir") + "/settings.json");
        String content = "";
        try {
            content = FileUtils.readFileToString(file, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject readObj = new JSONObject(content);
        darkmode = readObj.getBoolean("darkmode");
        emptyRecords = readObj.getBoolean("ShowEmptyRecords");
    }

    public boolean getDarkmode() {
        return this.darkmode;
    }

    public boolean getEmptyRecords() {
        return this.emptyRecords;
    }
}
