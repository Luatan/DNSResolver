package ch.luatan.DNSResolver.Data.API;

import ch.luatan.DNSResolver.DNSResolver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Nic extends API {
    private final String API_URL = "https://rdap.nic.ch/domain/";
    private String response;
    private final List<String> event;
    private final List<String> resNSDomain;
    private final List<String> resNSIP;
    private String[] resAddress;
    private String resDomain;
    private String resRegistrar;
    private String resStatus;

    public Nic() {
        event = new ArrayList<>();
        resNSDomain = new ArrayList<>();
        resNSIP = new ArrayList<>();

    }

    public List<String> query(String query) {
        response = request(API_URL + query);
        //Get Whole Object which icludes all Arrays
        if (response != null && responseCode == 200) {
            JSONObject jsonObj = new JSONObject(response);

            //get Domain name
            resDomain = jsonObj.getString("ldhName");

            //get into VcardArray -> vcard
            JSONArray checkVcard = jsonObj.getJSONArray("entities");
            if (checkVcard.length() > 0) {
                JSONArray vcard = jsonObj.getJSONArray("entities").getJSONObject(0).getJSONArray("vcardArray").getJSONArray(1);

                //get registrar
                resRegistrar = (String) vcard.getJSONArray(1).get(3);

                //get Adress add loop
                JSONArray adr = vcard.getJSONArray(2).getJSONArray(3);
                String[] address = new String[adr.length() - 2];
                int y = -1;
                for (int i = 0; i < vcard.getJSONArray(2).getJSONArray(3).length(); i++) {
                    if (!adr.get(i).equals("")) {
                        y++;
                        address[y] = (String) adr.get(i);
                    }
                }
                resAddress = address;
            }

            JSONArray status = jsonObj.getJSONArray("status");
            resStatus = status.toString().replaceAll("[^\\w]", "");

            if (jsonObj.getJSONArray("events").length() > 0) {
                JSONObject events = jsonObj.getJSONArray("events").getJSONObject(0);
                event.add(events.getString("eventAction"));
                event.add(events.getString("eventDate"));
            } else {
                event.add("registration");
                event.add("before 01. January 1996");
            }

            if (jsonObj.has("nameservers")) {
                JSONArray nameservers = jsonObj.getJSONArray("nameservers");

                for (int i = 0; i < nameservers.length(); i++) {
                    JSONObject ns = nameservers.getJSONObject(i);
                    resNSDomain.add(ns.getString("ldhName"));
                    JSONObject ips = ns.getJSONObject("ipAddresses");
                    if (ips.length() > 0) {
                        try {
                            if (ips.getJSONArray("v4").length() > 0) {
                                resNSIP.add(ips.getJSONArray("v4").getString(0));
                            } else if (ips.getJSONArray("v6").length() > 0) {
                                resNSIP.add(ips.getJSONArray("v6").getString(0));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                DNSResolver.LOGGER.debug("No NS");
            }
            return convertResultNic();
        } else {
            List<String> response = new ArrayList<>();
            response.add(checkResponseCode());
            return response;
        }
    }

    private List<String> convertResultNic() {
        List<String> res = new ArrayList<>();
        res.add("Domain: " + resDomain);
        res.add("Registrar: " + resRegistrar);

        //Address
        res.add("");
        if (resAddress != null) {
            res.add("Address:");
            res.add(resAddress[0]);
            res.add(resAddress[3] + "-" + resAddress[2] + " " + resAddress[1]);
        } else {
            res.add("No Address Found!");
        }

        res.add("");
        res.add("Status: " + resStatus);
        res.add(event.get(0) + ": " + event.get(1));

        //Nameservers
        if (resNSDomain.size() > 0) {
            res.add("");
            res.add("Nameservers:");
            for (int i = 0; i < resNSDomain.size(); i++) {
                if (resNSDomain.get(i) != null) {
                    if (i < resNSIP.size()) {
                        res.add("NS" + (i + 1) + ": " + resNSDomain.get(i) + "      " + "IP: " + resNSIP.get(i));
                    } else {
                        res.add("NS" + (i + 1) + ": " + resNSDomain.get(i));
                    }
                }
            }
        }
        return res;
    }

}
