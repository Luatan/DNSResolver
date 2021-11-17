import Controller.JSONController;
import Utils.FileStructure;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;


public class History {
    private final String FILENAME = "logs/history.json";
    private final JSONController handler;

    History() {
        handler = new JSONController(FILENAME);
    }

    private String[] readHistory() {
        JSONArray domainList = getArray();
        String[] history = new String[domainList.length()];
        for (int i = 0; i < domainList.length(); i++) {
            history[i] = domainList.getString(i);
        }
        return history;
    }

    private void removeIndex(int index) {
        JSONObject obj = new JSONObject(Objects.requireNonNull(FileStructure.readFile(FILENAME)));
        JSONArray domainList = obj.getJSONArray("domains");
        domainList.remove(index);
        handler.write(obj);
    }

    private void toTheEnd(int start_pos) {
        String domain = getDomain(start_pos);
        removeIndex(start_pos);
        JSONObject obj = new JSONObject(Objects.requireNonNull(FileStructure.readFile(FILENAME)));
        JSONArray domainList = obj.getJSONArray("domains");
        domainList.put(domain);
        handler.write(obj);
    }

    private int getIndex(String domain) {
        JSONArray domainList = getArray();
        for (int i = 0; i < domainList.length(); i++) {
            if (domainList.getString(i).toLowerCase().equals(domain)) {
                return i;
            }
        }
        System.err.println("domain not found");
        return 99;
    }

    private JSONArray getArray() {
        JSONObject object = new JSONObject(Objects.requireNonNull(FileStructure.readFile(FILENAME)));
        return object.getJSONArray("domains");
    }

    private boolean domainExists(String domain) {
        JSONArray domainList = getArray();
        for (int i = 0; i < domainList.length(); i++) {
            if (domainList.getString(i).toLowerCase().equals(domain)) {
                return true;
            }
        }
        return false;
    }

    private String getDomain(int index) {
        JSONArray domainList = getArray();
        return domainList.getString(index);
    }

    public void addDomain(String domain) {
        if (!domain.equals("")) {

            if (domainExists(domain)) {
                toTheEnd(getIndex(domain));
            } else {
                JSONObject object = new JSONObject(Objects.requireNonNull(FileStructure.readFile(FILENAME)));
                JSONArray domainList = object.getJSONArray("domains");
                domainList.put(domain.toLowerCase());
                //Write File
                handler.write(object);
            }
            if (readHistory().length > 10) {
                removeIndex(0);
            }
        }
    }

    public String[] getHistory() {
        return readHistory();
    }
}
