import okhttp3.Headers;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Objects;

public class IP_Info extends API {
    private final String PATH = System.getProperty("user.dir") + "/IP_API_req.json";
    private final String FILE = "IP_API_req.json";
    private final String URL = "http://ip-api.com/json/";
    private final JSONHandler JSON = new JSONHandler(FILE);
    private final int minRL= 10;
    private String info = "";
    private String IP = "";
    //private String fields = "53769";

    IP_Info(String IP) {
        this.IP = IP;
        if (checkTracker()) {
            info = super.request(buildURL(this.IP));
            writeTracker(responseHeaders);
        } else if (info.equals("")) {
            info = JSON.message("The Service is currently not available.");
        }
    }

    public String getInfo() {
        JSONObject jsonObj = new JSONObject(info);
        try {
            String query = jsonObj.getString("query"), isp = jsonObj.getString("isp"),
                    org = jsonObj.getString("org"), status = jsonObj.getString("status"),
                    as = jsonObj.getString("as"), country = jsonObj.getString("country"),
                    countryCode = jsonObj.getString("countryCode"), zip = jsonObj.getString("zip"),
                    city = jsonObj.getString("city"), region = jsonObj.getString("region"),
                    regionName = jsonObj.getString("regionName");

            double longitude = jsonObj.getDouble("lon"), latitude = jsonObj.getDouble("lat");

            String res =
                    "Query: " + query + "\nStatus: " + status + "\n\nNetwork Owner: " + isp + "\nNetwork Organisation: " +
                            org + "\n\nAddress:\n" + zip + " " + city + "\n" + regionName + " (" + region + ")\n" +
                            country + " (" + countryCode + ")\n" + "\n\n" + mapsLink(latitude, longitude);

            return res;
        } catch (JSONException e) {
            try {
                String query = jsonObj.getString("message");
                return query;
            } catch (JSONException y) {
                y.printStackTrace();
            }
        }
        return null;
    }

    private void writeTracker(Headers header) {
        int rl = Integer.parseInt(header.value(5));

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("lastrequest", System.currentTimeMillis());
        jsonObj.put("rl", rl);
        JSON.write(jsonObj);
    }

    private JSONObject readTracker() {
        return new JSONObject(Objects.requireNonNull(JSON.readFile()));
    }

    private boolean checkTracker() {
        File file = new File(PATH);
        if (file.exists()) {
            JSONObject obj = readTracker();
            int rl = obj.getInt("rl");
            long time = obj.getLong("lastrequest");
            long pastTime = (System.currentTimeMillis() - time) / 1000;

            if (pastTime > 60) {
                return true;
            } else {
                if (rl <= minRL) {
                    String message = "The Service is currently not available.\nPlease wait " + (60 - pastTime) + " Seconds to query again!";
                    info = JSON.message(message);
                    return false;
                } else {
                    return true;
                }
            }
        }
        return true;
    }

    private String buildURL(String IP) {
        return URL + IP;
    }

    private String buildURL(String IP, String fields) {
        return URL + IP + "?fields=" + fields;
    }

    private String mapsLink(double x, double y) {
        return "https://www.google.com/maps/search/?api=1&query=" + x + "," + y;
    }
}
