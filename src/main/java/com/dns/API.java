package com.dns;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class API {
    private String URL;
    private String domain;
    private boolean exists;
    private String resDomain;
    private String resRegistrar;
    private String[] resAddress;
    private String response;
    private int responseCode;
    private String resStatus;
    private String resRegistrationDate;
    private String [] resNSdomain;
    private String [] resNSIP;

    API(String URL, String domain) throws IOException {
        setURL(URL);
        setDomain(domain);
        response = GETReq();
    }

    public String GETReq() throws IOException{
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(URL + domain).build();
        try (Response response = client.newCall(request).execute()) {
            String res = response.body().string();
            responseCode = response.code();
            if (!res.equals("")) {
                exists = responseCode == 200;
                return res;
            }else {
                exists = false;
                return null;
            }
        }
    }

    public boolean domainExists(){
        return exists;
    }

    public String getNicchValues(){
        //Get Whole Object which icludes all Arrays
        if (response != null && responseCode == 200 && (this.URL + this.domain).equals("https://rdap.nic.ch/domain/" + this.domain)) {
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

            JSONArray status = jsonObj.getJSONArray("status");
            resStatus = status.toString().replaceAll("[^\\w]", "");


            System.out.println();
            if (jsonObj.getJSONArray("events").length() > 0) {
                JSONObject events = jsonObj.getJSONArray("events").getJSONObject(0);
                resRegistrationDate = events.getString("eventDate");
            } else {
                resRegistrationDate = "before 01 January 1996";
            }

            JSONArray nameservers = jsonObj.getJSONArray("nameservers");
            resNSdomain = new String[nameservers.length()];
            resNSIP = new String[nameservers.length()];
            for (int i = 0; i<nameservers.length(); i++) {
                JSONObject ns = nameservers.getJSONObject(i);
                resNSdomain[i] = ns.getString("ldhName");
                if (ns.getJSONObject("ipAddresses").length() > 0) {
                    resNSIP[i] = ns.getJSONObject("ipAddresses").getString("v4");
                }
            }
            return convertResultNic();
        } else if (responseCode != 200) {
            return checkResponseCode();
        }
        return null;
    }

    private String checkResponseCode() {
        switch (responseCode){
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

    private String convertResultNic(){
        //Address
        String addressString = "Adresse: \n" + resAddress[0] + "\n" + resAddress[3] + "-" + resAddress[2] + " " + resAddress[1];

        //Nameservers
        StringBuilder nsString = new StringBuilder("\nNameserver: \n");
        for (int i = 0; i<resNSdomain.length; i++) {
            nsString.append("NS ").append(i + 1).append(": ");
            nsString.append(resNSdomain[i]).append("\t");
            if (resNSIP[i] != null) {
                nsString.append("IP: ");
                nsString.append(resNSIP[i]).append("\n");
            }else {
                nsString.append("\n");
            }
        }
        return "Domain: " + resDomain + "\n" +
                "Registrar: " + resRegistrar + "\n\n" + addressString + "\n\nStatus: " + resStatus
                + "\nFirst Registration: " + resRegistrationDate + "\n" + nsString;
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

    private boolean isIPAdress(String domain){
        Matcher m = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$").matcher(domain);
        return m.find();
    }
}
