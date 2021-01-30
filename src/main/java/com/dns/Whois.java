package com.dns;


import org.apache.commons.net.whois.WhoisClient;

import java.io.IOException;

public class Whois {

    public String getWhois(String domainName, String whoisServer, int port) {

        StringBuilder result = new StringBuilder();

        WhoisClient whois = new WhoisClient();
        try {

            whois.connect(whoisServer, port);
            String whoisData1 = whois.query(domainName);

            result.append(whoisData1);
            whois.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Domain Check: \n" + result.toString();

    }

    public String getWhois(String domainName, String whoisServer) {

        StringBuilder result = new StringBuilder();

        WhoisClient whois = new WhoisClient();
        try {

            //Port 43
            whois.connect(whoisServer, whois.getDefaultPort());
            String whoisData1 = whois.query(domainName);

            result.append(whoisData1);
            whois.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "\nDomain Check: \n" + result.toString();

    }

}
