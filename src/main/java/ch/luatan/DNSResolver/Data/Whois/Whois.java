package ch.luatan.DNSResolver.Data.Whois;

import ch.luatan.DNSResolver.DNSResolver;
import ch.luatan.DNSResolver.Data.API.API;
import ch.luatan.DNSResolver.Model.Utils.Domain;
import org.apache.commons.net.whois.WhoisClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Whois extends API {
    private final String whoisServer;

    public Whois(String whoisServer) {
        this.whoisServer = whoisServer;
    }

    public static List<String> getWhois(String domainName, String whoisServer) {
        WhoisClient whois = new WhoisClient();
        String whoisData = "";
        try {
            //Port 43
            DNSResolver.LOGGER.debug("Connecting to " + whoisServer + " on Port " + whois.getDefaultPort());
            whois.connect(whoisServer, whois.getDefaultPort());
            // Handles DE Domains (special params for full info)
            if (Domain.getExtension(domainName).equals(".de")) {
                DNSResolver.LOGGER.debug("WHOIS query: " + "-T dn " + domainName);
                whoisData = whois.query("-T dn " + domainName);
            } else {
                DNSResolver.LOGGER.debug("WHOIS query: " + domainName);
                whoisData = whois.query(domainName);
            }
            whois.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
            DNSResolver.LOGGER.error(e.getMessage());
        }

        whoisData = whoisData.replaceAll("([%#].*[\\S\\s])|(.*REDACTED.*[\\S\\s])|(For more info.[\\S\\s]*)", "").replaceAll("\r", "");
        //split by lines
        String[] tokens = whoisData.split("\\n");

        return Arrays.asList(tokens);
    }

    @Override
    public List<String> query(String query) {
        return getWhois(query, this.whoisServer);
    }
}
