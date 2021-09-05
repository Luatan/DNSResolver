package Controller;

import Utils.Files;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class JSONController {

    private String filename;

    public JSONController(String filename) {
        this.filename = filename;
    }

    public void write(JSONObject object) {
        try {
            FileWriterWithEncoding file = new FileWriterWithEncoding(Files.DIR_HOME + filename, "utf-8");
            file.write(object.toString(4));
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean getBoolValue(String key) {
        JSONObject readObj = new JSONObject(Objects.requireNonNull(Files.readFile(filename)));
        return readObj.getBoolean(key);
    }

    public String getStringValue(String key) {
        JSONObject readObj = new JSONObject(Objects.requireNonNull(Files.readFile(filename)));
        return readObj.getString(key);
    }

    public String message(String message) {
        JSONObject obj = new JSONObject();
        obj.put("message", message);
        return obj.toString();
    }
}
