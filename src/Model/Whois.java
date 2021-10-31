package Model;

import org.apache.commons.net.whois.WhoisClient;

import java.io.IOException;

public class Whois {

    public String getWhois(String domainName, String whoisServer) {
        WhoisClient whois = new WhoisClient();
        String whoisData = "";
        try {
            //Port 43
            whois.connect(whoisServer, whois.getDefaultPort());
            whoisData = whois.query(domainName);
            whois.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
        whoisData = whoisData.replaceAll("(%.*)|(#.*)|(.*REDACTED.*)|(For more info.[\\S\\s]*)", "");
        return whoisData.trim();

    }

}
