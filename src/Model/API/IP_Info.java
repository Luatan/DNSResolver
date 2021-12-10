package Model.API;

import Model.JsonAdapter;
import Utils.Config;
import Utils.FileStructure;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import okhttp3.Headers;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IP_Info extends API {
    private final String URL = "http://ip-api.com/json/";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Map<String, Long> log;
    private String api_output;
    //private String fields = "53769";

    public IP_Info(String ip_addr) {
        readTracker();
        if (isAllowed()) {
            api_output = super.request(buildURL(ip_addr));
            writeTracker(responseHeaders);
        } else if (api_output.equals("")) {
            api_output = message("The Service is currently not available.");
        }
    }

    public String getOutput() {
        JSONObject jsonObj = new JSONObject(api_output);
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


            String result =
                    "Query: " + query + "\nStatus: " + status + "\n\nNetwork Owner: " + isp + "\nNetwork Organisation: " +
                            org + "\n\nAddress:\n" + zip + " " + city + "\n" + regionName + " (" + region + ")\n" +
                            country + " (" + countryCode + ")\n" + "\nMore Information about this Network Owner:\n" + asLink + "\n\n" + mapsLink(latitude, longitude);

            return result;
        } catch (JSONException e) {
            try {
                return jsonObj.getString("message");
            } catch (JSONException y) {
                y.printStackTrace();
            }
        }
        return null;
    }

    private void writeTracker(Headers header) {
        // get RL from header of request
        long rl = Integer.parseInt(header.value(5));

        // update values
        log.put("lastrequest", System.currentTimeMillis());
        log.put("rl", rl);

        //write to json
        JsonAdapter.write(log, Config.IP_API_LOG_FILE);
    }

    private void readTracker() {
        try {
            Reader reader = FileStructure.getReader(Config.IP_API_LOG_FILE);
            log = gson.fromJson(reader, new TypeToken<Map<String, Long>>() {}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isAllowed() {
        if (FileStructure.fileExists(Config.IP_API_LOG_FILE)) {
            long rl = log.get("rl");

            // Calculate Time since last request
            long time = log.get("lastrequest");
            long pastTime = (System.currentTimeMillis() - time) / 1000;

            // pass if exceed 1 min
            if (pastTime > 60) {
                return true;
            }
            int MINRL = 16;
            if (rl <= MINRL) {
                api_output = message("The Service is currently not available.\nPlease wait " + (60 - pastTime) + " Seconds to query again!");
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

    private String message(String message) {
        JSONObject obj = new JSONObject();
        obj.put("message", message);
        return obj.toString();
    }
}
