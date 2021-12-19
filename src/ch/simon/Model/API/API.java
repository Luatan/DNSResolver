package ch.simon.Model.API;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

public abstract class API {
    public Headers responseHeaders;
    public int responseCode = 500;

    protected String request(String url) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            String res = response.body().string();
            responseHeaders = response.headers();
            responseCode = response.code();
            if (!res.equals("")) {
                return res;
            } else {
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Query failed";
    }

    protected String checkResponseCode() {
        switch (responseCode) {
            case 200:
                return "200 - OK";
            case 404:
                return "404 - Not Found";
            case 400:
                return "400 - Bad Request";
            case 401:
                return "401 - Bad Request";
            case 403:
                return "403 - Forbidden";
            case 429:
                return "429 - Too Many Requests";
            default:
                return "Invalid Statuscode";
        }
    }

    protected abstract List<String> getOutput();
}
