package Model;

import Utils.Domain;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NIC extends API {
    private final String RESPONSE;
    private final String API_URL = "https://rdap.nic.ch/domain/";;
    private String domain;
    private String resDomain;
    private String resRegistrar;
    private String resRegistrationDate;
    private String resStatus;
    private String[] resAddress;
    private String[] resNSDomain;
    private String[] resNSIP;

    public NIC(String domain) {
        setDomain(domain);
        RESPONSE = super.request(API_URL + domain);
    }

    public String getNicValues() {
        //Get Whole Object which icludes all Arrays
        if (RESPONSE != null && super.responseCode == 200) {
            JSONObject jsonObj = new JSONObject(RESPONSE);
            //System.out.println(jsonObj.toString(4));
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
                resRegistrationDate = events.getString("eventDate");
            } else {
                resRegistrationDate = "before 01 January 1996";
            }
            JSONArray nameservers = jsonObj.getJSONArray("nameservers");
            resNSDomain = new String[nameservers.length()];
            resNSIP = new String[nameservers.length()];
            for (int i = 0; i < nameservers.length(); i++) {
                JSONObject ns = nameservers.getJSONObject(i);
                resNSDomain[i] = ns.getString("ldhName");
                JSONObject ips = ns.getJSONObject("ipAddresses");
                if (ips.length() > 0) {
                    try {
                        if (ips.getJSONArray("v4").length() > 0) {
                            resNSIP[i] = ips.getJSONArray("v4").getString(0);
                        } else if (ips.getJSONArray("v6").length() > 0) {
                            resNSIP[i] = ips.getJSONArray("v6").getString(0);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            return convertResultNic();
        } else if (super.responseCode != 200) {
            return super.checkResponseCode();
        }
        return null;
    }

    private String convertResultNic() {
        //Address
        String addressString;
        if (resAddress != null) {
            addressString = "Address: \n" + resAddress[0] + "\n" + resAddress[3] + "-" + resAddress[2] + " " + resAddress[1];
        } else {
            addressString = "No Address found";
        }

        //Nameservers
        StringBuilder nsString = new StringBuilder("\nNameservers: \n");
        for (int i = 0; i < resNSDomain.length; i++) {
            if (resNSDomain[i] != null) {
                nsString.append("NS ").append(i + 1).append(": ");
                nsString.append(resNSDomain[i]).append("\t");
            }
            if (resNSIP[i] != null) {
                nsString.append("IP: ");
                nsString.append(resNSIP[i]).append("\n");
            } else {
                nsString.append("\n");
            }
        }
        return "Domain: " + resDomain + "\n" +
                "Registrar: " + resRegistrar + "\n\n" + addressString + "\n\nStatus: " + resStatus
                + "\nFirst Registration: " + resRegistrationDate + "\n" + nsString;
    }

    private void setDomain(String domain) {
        if (!Domain.isIPAdress(domain)) {
            this.domain = domain;
        } else {
            System.out.println("This is not a Domain");
        }
    }

}
