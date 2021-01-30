package com.dns;

import com.sun.xml.internal.bind.v2.TODO;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class API {
    private String URL = "https://rdap.nic.ch/domain/";
    private String domain;
    private String resDomain;
    private String resRegistrar;
    private String[] resAddress;
    private Response response;

    API(String URL, String domain) throws IOException {
        setURL(URL);
        setDomain(domain);
        getValuesFromJSON(GETReq());
    }

    public String GETReq() throws IOException{
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(URL+ domain).build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    private void getValuesFromJSON(String response){
        //Get Whole Object which icludes all Arrays
        JSONObject jsonObj = new JSONObject(response);

        //get Domain name
        resDomain = jsonObj.getString("ldhName");

        //get into VcardArray -> vcard
        JSONArray vcard = jsonObj.getJSONArray("entities").getJSONObject(0).getJSONArray("vcardArray").getJSONArray(1);

        //get registrar
        resRegistrar = (String) vcard.getJSONArray(1).get(3);

        //get Adress add loop
        JSONArray adr = vcard.getJSONArray(2).getJSONArray(3);
        String[] address = new String[adr.length()-2];
        int y = -1;
        for (int i = 0; i < vcard.getJSONArray(2).getJSONArray(3).length(); i++) {
            if (!adr.get(i).equals("")) {
                y++;
                address[y] = (String) adr.get(i);
            }
        }
        resAddress = address;

        // TODO: 30.01.2021 Nameserver abfragen WHOIS von nic.ch


    }

    private void setURL(String URL){
        this.URL = URL;
    }

    private void setDomain(String domain){
        if (!isIPAdress(domain)) {
            this.domain = domain;
        } else {
            System.out.println("This is not a Domain");
        }
    }

    public String buildString(){
        StringBuilder result = new StringBuilder();

        result.append("Domain: " + resDomain + "\n");
        result.append("Registrar: " + resRegistrar + "\n");
        result.append(resAddress[0] + resAddress[1] + "\n" + resAddress[3] + "-" + resAddress[2]);
        return result.toString();
    }

    private boolean isIPAdress(String domain){
        Matcher m = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$").matcher(domain);
        return m.find();
    }
}
