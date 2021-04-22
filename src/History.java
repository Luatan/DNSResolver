import org.json.JSONArray;
import org.json.JSONObject;

public class History{
    private final String PATH = "history.json";
    private JSONHandler handler;
    private String[] history;


    History() {
        handler = new JSONHandler(PATH);
        init();
        addDomain("test4");
    }

    private boolean init() {
        if (handler.fileExists()) {

            return true;
        }
        writeDefaultHistory();
        return false;
    }

    private void writeDefaultHistory() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("domains", new String[]{});
        handler.write(jsonObj);
    }

    private String[] readHistory() {
        JSONArray domainList = getArray();
        String[] history = new String[domainList.length()];
        for (int i = 0; i < domainList.length(); i++) {
            history[i] = domainList.getString(i);
        }
        return history;
    }

    public void removeIndex(int index) {
        JSONObject obj = new JSONObject(handler.readFile());
        JSONArray domainList = obj.getJSONArray("domains");
        domainList.remove(index);
        handler.write(obj);
    }

    public void toTheEnd(int start_pos) {
        String domain = getDomain(start_pos);
        removeIndex(start_pos);
        JSONObject obj = new JSONObject(handler.readFile());
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
        JSONObject object = new JSONObject(handler.readFile());
        JSONArray domainList = object.getJSONArray("domains");
        return domainList;
    }

    private boolean domainExists(String domain){
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
                String content = handler.readFile();
                JSONObject object = new JSONObject(content);
                JSONArray domainList = object.getJSONArray("domains");
                domainList.put(domain);
                //Write File
                handler.write(object);
            }

        }
    }



    public String[] getHistory(){
        return readHistory();
    }
}
