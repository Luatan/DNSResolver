import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Whois_NIC extends API {
    private final String RESPONSE;
    private String domain;
    private String resDomain;
    private String resRegistrar;
    private String resRegistrationDate;
    private String resStatus;
    private String URL;
    private String[] resAddress;
    private String[] resNSDomain;
    private String[] resNSIP;

    Whois_NIC(String URL, String domain) {
        this.URL = URL;
        setDomain(domain);
        RESPONSE = super.request(URL + domain);
    }

    public String getNicValues() {
        //Get Whole Object which icludes all Arrays
        if (RESPONSE != null && super.responseCode == 200 && (this.URL + this.domain).equals("https://rdap.nic.ch/domain/" + this.domain)) {
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
                if (ns.getJSONObject("ipAddresses").length() > 0) {
                    resNSIP[i] = ns.getJSONObject("ipAddresses").getString("v4");
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
        if (!isIPAdress(domain)) {
            this.domain = domain;
        } else {
            System.out.println("This is not a Domain");
        }
    }

    private boolean isIPAdress(String domain) {
        Matcher m = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$").matcher(domain);
        return m.find();
    }
}
