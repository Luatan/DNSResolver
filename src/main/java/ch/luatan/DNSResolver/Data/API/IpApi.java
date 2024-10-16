package ch.luatan.DNSResolver.Data.API;

import ch.luatan.DNSResolver.DNSResolver;
import ch.luatan.DNSResolver.Model.Utils.JsonAdapter;
import ch.luatan.DNSResolver.Model.Utils.Config;
import ch.luatan.DNSResolver.Model.Utils.FileHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import okhttp3.Headers;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IpApi extends API {
    private final String URL = "http://ip-api.com/json/";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Map<String, Long> log;
    private String api_output;

    public IpApi() {
        log = new LinkedHashMap<>();
        readTracker();
    }

    public List<String> query(String query) {
        if (isAllowed()) {
            api_output = super.request(buildURL(query));
            writeTracker(responseHeaders);
        } else if (api_output.equals("")) {
            api_output = message("The Service is currently not available.");
        }
        Map<String, String> output = JsonAdapter.HANDLER.fromJson(api_output, new TypeToken<Map<String, String>>() {
        }.getType());

        List<String> res = new ArrayList<>();

        if (output.get("status").equals("fail")) {
            res.add("Status: " + output.get("status"));
            res.add("");
            res.add("Message: " + output.get("message"));

            return res;
        }

        Matcher matcher = Pattern.compile("AS\\d+").matcher(output.get("as"));
        if (matcher.find()) {
            output.put("as", matcher.group());
        }

        res.add("Status: " + output.get("status"));

        res.add(""); // new Line
        res.add("Network Owner: " + output.get("isp"));
        res.add("Network Organisation: " + output.get("org"));

        //Address
        res.add(""); // new Line
        res.add("Address:");
        res.add(output.get("zip") + " " + output.get("city"));
        res.add(output.get("regionName") + " (" + output.get("region") + ")");
        res.add(output.get("country") + " (" + output.get("countryCode") + ")");

        res.add(""); // new Line
        res.add("More Information about this Network Owner:");
        res.add("https://apps.db.ripe.net/db-web-ui/query?searchtext=" + output.get("as"));

        res.add(""); // new Line
        res.add("Google Maps (Precision depends on the IP Address):");
        res.add(mapsLink(Double.parseDouble(output.get("lat")), Double.parseDouble(output.get("lon"))));

        return res;
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
        if (!FileHelper.fileExists(Config.IP_API_LOG_FILE)) {
            DNSResolver.LOGGER.error("File: " + Config.IP_API_LOG_FILE + " does not exist!");
            return;
        }
        try {
            Reader reader = FileHelper.getReader(Config.IP_API_LOG_FILE);
            log = gson.fromJson(reader, new TypeToken<Map<String, Long>>() {
            }.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isAllowed() {
        if (FileHelper.fileExists(Config.IP_API_LOG_FILE)) {
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
