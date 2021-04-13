import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class IP_Info {
    private final String PATH = System.getProperty("user.dir") + "/IP_API_req.json";
    private String info = "";
    private String URL = "http://ip-api.com/json/";
    private String IP = "";
    //private String fields = "?fields=53769";
    private int responseCode = 500;

    IP_Info(String IP) {
        this.IP = IP;
        if (checkTrackerJSON()) {
            info = request();
        } else if (info.equals("")){
            info = new JSONObject().put("message", "The Service is currently not available.").toString();
        }

    }

    private String request() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(URL + IP).build();
        try (Response response = client.newCall(request).execute()) {
            String res = response.body().string();
            writeTrackerJSON(response.headers());

            responseCode = response.code();
            //System.out.println(responseCode);
            if (!res.equals("")) {
                return res;
            } else {

                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Query failed";
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

    private String mapsLink(double x, double y) {
        return "https://www.google.com/maps/search/?api=1&query=" + x + "," + y;
    }

    private void writeTrackerJSON(Headers header) {
        int rl = Integer.parseInt(header.value(5));

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("lastrequest", System.currentTimeMillis());
        jsonObj.put("rl", rl);

        try {
            FileWriter newfile = new FileWriter(PATH);
            newfile.write(jsonObj.toString(4));
            newfile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkTrackerJSON() {
        File file = new File(PATH);
        if (file.exists()) {
            JSONObject obj = readTrackerJSON();
            int rl = obj.getInt("rl");
            long time = obj.getLong("lastrequest");
            long pastTime = (System.currentTimeMillis() - time)/1000;

            if (pastTime > 60) {
                return true;
            } else {
                if (rl <= 10) {
                    String message = "The Service is currently not available.\nPlease wait " + (60 - pastTime) + " Seconds to query again!";
                    info = mesageJSON(message);
                    return false;
                } else {
                    return true;
                }
            }

        }
        return true;
    }

    private JSONObject readTrackerJSON() {
        File file = new File(PATH);
        try {
            String content = FileUtils.readFileToString(file, "utf-8");
            return new JSONObject(Objects.requireNonNull(content));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject().put("message", "No JSON File Found");
    }

    private String mesageJSON(String message) {
        JSONObject obj = new JSONObject();
        obj.put("message", message);
        return obj.toString();
    }
}
