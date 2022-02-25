package ch.luatan.DNSResolver.Model.Whois;

import org.apache.commons.net.whois.WhoisClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Whois {

    public List<String> getWhois(String domainName, String whoisServer) {
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

        //remove comments
        whoisData = whoisData.replaceAll("([%#].*[\\S\\s])|(.*REDACTED.*[\\S\\s])|(For more info.[\\S\\s]*)", "").replaceAll("\r", "");
        //split by lines
        String[] tokens = whoisData.split("\\n");

        return Arrays.asList(tokens);
    }

}
