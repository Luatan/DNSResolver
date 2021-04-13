import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;

public class IP_Info {
    private String info = "";
    private String URL = "http://ip-api.com/json/";
    private String IP = "";
    private String fields = "?fields=53769";
    private int responseCode = 500;

    IP_Info(String IP) {
        this.IP = IP;
        info = request();
    }

    private String request() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(URL + IP).build();
        try (Response response = client.newCall(request).execute()) {
            String res = response.body().string();
            trackRequests(response.headers());

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
        return null;
    }

    public String getInfo() {
        JSONObject jsonObj = new JSONObject(info);
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
                        country + " (" + countryCode + ")\n" + "\n" + mapsLink(latitude, longitude);

        return res;
    }

    private String mapsLink(double x, double y) {
        return "https://www.google.com/maps/search/?api=1&query=" + x + "," + y;
    }

    private void trackRequests(Headers header) {
        String ttl = header.value(4);
        String rl = header.value(5);


    }

}
