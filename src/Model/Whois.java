package Model;

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
        whoisData = whoisData.replaceAll("(%.*)|(#.*)|(.*REDACTED.*)|(For more info.[\\S\\s]*)", "");

        String[] tokens = whoisData.split("\\n");

        return Arrays.asList(tokens);

    }

}
