import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class JSONHandler {

    private final String BASEPATH = System.getProperty("user.dir") + "/";
    private String filename = "";

    JSONHandler (String filename) {
        this.filename = filename;
    }

    public String readFile() {
        File file = new File(BASEPATH + filename);
        try {
            return FileUtils.readFileToString(file, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void write(JSONObject object) {
        try {
            FileWriter newfile = new FileWriter(BASEPATH + filename);
            newfile.write(object.toString(4));
            newfile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean getBoolValue(String key) {
        JSONObject readObj = new JSONObject(Objects.requireNonNull(readFile()));
        return readObj.getBoolean(key);
    }

    public String getStringValue(String key) {
        JSONObject readObj = new JSONObject(Objects.requireNonNull(readFile()));
        return readObj.getString(key);
    }

    public String message(String message) {
        JSONObject obj = new JSONObject();
        obj.put("message", message);
        return obj.toString();
    }
}
