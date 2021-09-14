package Model;

import Controller.JSONController;
import Utils.FileStructure;
import okhttp3.Headers;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IP_Info extends API {
    private final String FILENAME = "logs/IP_API_req.json";
    private final String URL = "http://ip-api.com/json/";
    private final JSONController JSON = new JSONController(FILENAME);
    private final int MINRL = 16;
    private String info = "";
    private String ip = "";
    //private String fields = "53769";

    public IP_Info(String ip) {
        this.ip = ip;
        if (checkTracker()) {
            info = super.request(buildURL(this.ip));
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
            Pattern pattern = Pattern.compile("AS\\d+");
            Matcher matcher = pattern.matcher(as);

            if (matcher.find()) {
                as = matcher.group();
            }

            String asLink = "https://apps.db.ripe.net/db-web-ui/query?searchtext=" + as;


            String res =
                    "Query: " + query + "\nStatus: " + status + "\n\nNetwork Owner: " + isp + "\nNetwork Organisation: " +
                            org + "\n\nAddress:\n" + zip + " " + city + "\n" + regionName + " (" + region + ")\n" +
                            country + " (" + countryCode + ")\n" + "\nMore Information about this Network Owner:\n" + asLink + "\n\n" + mapsLink(latitude, longitude);

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

        System.out.println(header);
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("lastrequest", System.currentTimeMillis());
        jsonObj.put("rl", rl);
        JSON.write(jsonObj);
    }

    private JSONObject readTracker() {
        return new JSONObject(Objects.requireNonNull(FileStructure.readFile(FILENAME)));
    }

    private boolean checkTracker() {
        if (FileStructure.fileExists(FILENAME)) {
            JSONObject obj = readTracker();
            int rl = obj.getInt("rl");
            long time = obj.getLong("lastrequest");
            long pastTime = (System.currentTimeMillis() - time) / 1000;

            if (pastTime > 60) {
                return true;
            }
            if (rl <= MINRL) {
                String message = "The Service is currently not available.\nPlease wait " + (60 - pastTime) + " Seconds to query again!";
                info = JSON.message(message);
                return false;
            }
        }
        return true;
    }

    private String buildURL(String ip) {
        return URL + ip;
    }

    private String buildURL(String ip, String fields) {
        return URL + ip + "?fields=" + fields;
    }

    private String mapsLink(double x, double y) {
        return "https://www.google.com/maps/search/?api=1&query=" + x + "," + y;
    }
}
