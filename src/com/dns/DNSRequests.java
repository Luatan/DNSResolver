package com.dns;

import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class DNSRequests {
    private String hostname;
    private String IP;
    private String[] A;
    private String[] AAAA;
    private String[] CNAME;
    private String[] MX;
    private String[] NS;
    private String[] TXT;
    private String[] SRV;
    private String[] SOA;
    private String typeSet;

    DNSRequests(String value, String type) throws UnknownHostException, NamingException {
        setHost(value);
        setRecords(type);

        if (isSubdomain(hostname)) {
            String origHost = hostname;
            String[] parts = origHost.split("[.]");
            this.hostname = parts[parts.length - 2] + "." + parts[parts.length - 1];
            setRecords("NS");
            this.hostname = origHost;
        } else {
            setRecords("NS");
        }
    }

    DNSRequests() {

    }

    public boolean isSubdomain(String host) {
        return host.split("[.]", 3).length > 2;
    }

    public String getExtension(String hostname) {
        String[] host = hostname.split("[.]");
        return host[host.length - 1];
    }

    private void setHost(String host) {
        try {
            InetAddress inetHost = InetAddress.getByName(host);
            hostname = inetHost.getHostName();
            IP = inetHost.getHostAddress();

        } catch (UnknownHostException ex) {
            hostname = "Unrecognized host";
            System.err.println("Unrecognized host");
        }
    }

    private void setAllRecords() throws NamingException, UnknownHostException {
        setRecords("A");
        setRecords("AAAA");
        setRecords("CNAME");
        setRecords("MX");
        setRecords("TXT");
        setRecords("SRV");
        setRecords("SOA");
    }

    private void setRecords(String type) throws NamingException, UnknownHostException {
        typeSet = type;
        try {
            InitialDirContext iDirC = new InitialDirContext();
            // get all the DNS records for hostname
            Attributes attributes = iDirC.getAttributes("dns:/" + hostname, new String[]{type});

            if (type.matches("[*]")) {
                setAllRecords();
            } else {
                try {
                    //Get the Records
                    String[] listRecords = attributes.get(type).toString().split("(,)( )");
                    String[] tempRecords = listRecords[0].split(": ", 2);
                    //Replace first char with the actual value instead of the type
                    listRecords[0] = tempRecords[1];

                    populateRecords(listRecords);

                } catch (Exception e) {
                    //System.out.println("No Records found");
                }
            }
        } catch (NameNotFoundException e) {
            System.err.println("No DNS Found - DNSRequests (SetRecords)");
        }

    }

    private void populateRecords(String[] listRecords) {
        String type = typeSet;
        switch (type) {
            case "A":
                A = listRecords;
                break;
            case "AAAA":
                AAAA = listRecords;
                break;
            case "CNAME":
                CNAME = listRecords;
            case "MX":
                MX = listRecords;
            case "TXT":
                TXT = listRecords;
                break;
            case "SOA":
                SOA = listRecords;
                break;
            case "NS":
                NS = listRecords;
                break;
            case "SRV":
                SRV = listRecords;
                break;
            default:
                System.err.println("type not found");
                break;
        }
    }

    public String[] getRecords(String type) {
        switch (type) {
            case "A":
                return A;
            case "AAAA":
                return AAAA;
            case "CNAME":
                return CNAME;
            case "MX":
                return MX;
            case "TXT":
                return TXT;
            case "SOA":
                return SOA;
            case "NS":
                return NS;
            case "SRV":
                return SRV;
            default:
                System.err.println("Type was not found");
                break;
        }
        return null;
    }

    public String getHostname() {
        return hostname;
    }

    public String getIP() {
        return IP;
    }
}