package Model.API;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NIC extends API {
    private final String API_URL = "https://rdap.nic.ch/domain/";
    private final String RESPONSE;
    private final ArrayList<String> event;
    private final ArrayList<String> resNSDomain;
    private final ArrayList<String> resNSIP;
    private String[] resAddress;
    private String resDomain;
    private String resRegistrar;
    private String resStatus;

    public NIC(String domain) {
        event = new ArrayList();
        resNSDomain = new ArrayList<>();
        resNSIP = new ArrayList<>();
        RESPONSE = request(API_URL + domain);
    }

    public List<String> getOutput() {
        //Get Whole Object which icludes all Arrays
        if (RESPONSE != null && responseCode == 200) {
            JSONObject jsonObj = new JSONObject(RESPONSE);

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
                System.out.println("No NS");
            }
            return convertResultNic();
        }

        return null;
    }

    private List<String> convertResultNic() {
        //Address
        String addressString;
        if (resAddress != null) {
            addressString = "Address: \n" + resAddress[0] + "\n" + resAddress[3] + "-" + resAddress[2] + " " + resAddress[1];
        } else {
            addressString = "No Address found";
        }


        //Nameservers
        StringBuilder nsString = new StringBuilder();
        if (resNSDomain.size() > 0) {
            nsString.append("\nNameservers: \n");
            for (int i = 0; i < resNSDomain.size(); i++) {
                if (resNSDomain.get(i) != null) {
                    nsString.append("NS").append(i + 1).append(": ");
                    nsString.append(resNSDomain.get(i)).append("\t");
                }
                if (i < resNSIP.size()) {
                    nsString.append("IP: ");
                    nsString.append(resNSIP.get(i)).append("\n");
                } else {
                    nsString.append("\n");
                }
            }
        }

        List<String> res = new ArrayList<>();
        res.add("Domain: " + resDomain);
        res.add("Registrar: " + resRegistrar);
        res.add("");
        res.add(addressString);
        res.add("");
        res.add("Status: " + resStatus);
        res.add(event.get(0) + ": " + event.get(1));
        res.add(nsString.toString());

        return res;
    }

}
