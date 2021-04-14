import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class API {
    public Headers responseHeaders;
    public int responseCode = 500;

    String request(String url) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            String res = response.body().string();
            responseHeaders = response.headers();
            responseCode = response.code();
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

    String checkResponseCode() {
        switch (responseCode) {
            case 200:
                return "200 - OK";
            case 404:
                return "404 - Not Found";
            case 400:
                return "400 - Bad Request";
            case 429:
                return "429 - Too Many Requests";
            default:
                return "Invalid Statuscode";
        }
    }
}
