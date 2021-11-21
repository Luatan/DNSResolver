package Utils;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class Json {

    private final String FILENAME;

    public Json(String filename) {
        this.FILENAME = filename;
    }

    public void write(JSONObject object) {
        try {
            FileWriterWithEncoding file = new FileWriterWithEncoding(FileStructure.DIR_HOME + FILENAME, "utf-8");
            file.write(object.toString(4));
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean getBoolValue(String key) {
        JSONObject readObj = new JSONObject(Objects.requireNonNull(FileStructure.readFile(FILENAME)));
        return readObj.getBoolean(key);
    }

    public String getStringValue(String key) {
        JSONObject readObj = new JSONObject(Objects.requireNonNull(FileStructure.readFile(FILENAME)));
        return readObj.getString(key);
    }

    public String message(String message) {
        JSONObject obj = new JSONObject();
        obj.put("message", message);
        return obj.toString();
    }
}
