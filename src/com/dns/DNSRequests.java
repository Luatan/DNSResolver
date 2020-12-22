package com.dns;

import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

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

    DNSRequests(String value, String type) throws UnknownHostException, NamingException {
        setHost(value.toLowerCase().replace(" ", ""));
        setRecords(type);
    }

    DNSRequests() {
    }

    private void setHost(String host) throws NamingException, UnknownHostException {
        try {
            InetAddress inetHost = InetAddress.getByName(host);
            hostname = inetHost.getHostName();
            IP = inetHost.getHostAddress();

        } catch (UnknownHostException ex) {
            hostname = host;
            System.err.println("This host: " + host + " has no IP Address");
        }
        setNameServer();
    }

    private void setNameServer() throws UnknownHostException, NamingException {
        if (isSubdomain(hostname)) {
            String origHost = hostname;
            this.hostname = getMainDomain(hostname);
            setRecords("NS");
            this.hostname = origHost;
        } else {
            setRecords("NS");
        }
    }

    private void setAllRecords() throws NamingException, UnknownHostException {
        String[] recordsToUse = {"A", "AAAA", "CNAME", "MX", "SOA", "SRV", "TXT"};
        for (String record : recordsToUse) {
            setRecords(record);
        }
    }

    private void setRecords(String type) throws NamingException, UnknownHostException {
        if (!hostname.equals("Unrecognized host")) {
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
                        //Replace first char with the actual value instead of the type
                        listRecords[0] = listRecords[0].split(": ", 2)[1];
                        populateRecords(listRecords, type);
                    } catch (Exception e) {
                        //System.err.println("No Records for " + type + " in " + hostname + " found!");
                    }
                }
            } catch (NameNotFoundException e) {
                System.err.println("No DNS-Records Found in " + hostname + " at DNSRequests.java (SetRecords)");
            }
        }
    }

    private void populateRecords(String[] RecordList, String type) {
        switch (type) {
            case "A":
                A = RecordList;
                break;
            case "AAAA":
                AAAA = RecordList;
                break;
            case "CNAME":
                CNAME = RecordList;
                break;
            case "MX":
                MX = RecordList;
                break;
            case "SOA":
                SOA = RecordList;
                break;
            case "NS":
                NS = RecordList;
                break;
            case "TXT":
                TXT = RecordList;
                break;
            case "SRV":
                SRV = RecordList;
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
            case "SOA":
                //Format:
                String[] temp = SOA[0].split(" ");

                temp[2] += "\t\t\t serialnumber";
                temp[3] += "\t\t\t\t refresh (" + getTimeFromSeconds(Integer.valueOf(temp[3])) + ")";
                temp[4] += "\t\t\t\t retry (" + getTimeFromSeconds(Integer.valueOf(temp[4])) + ")";
                temp[5] += "\t\t\t expire (" + getTimeFromSeconds(Integer.valueOf(temp[5])) + ")";
                temp[6] += "\t\t\t minimum (" + getTimeFromSeconds(Integer.valueOf(temp[6])) + ")";
                System.out.println(Arrays.toString(temp));
                return temp;
            case "NS":
                return NS;
            case "SRV":
                return SRV;
            case "TXT":
                return TXT;
            default:
                System.err.println("Type was not found");
                break;
        }
        return null;
    }

    private String getTimeFromSeconds(int time){
        time = time/3600;
        if (time > 1) {
            return time + " hours";
        }
        return time + " hour";
    }

    public boolean isSubdomain(String host) {
        return host.split("[.]", 3).length > 2;
    }

    public String getExtension(String hostname) {
        String[] host = hostname.split("[.]");
        return host[host.length - 1];
    }

    public String getMainDomain(String host) {
        String[] partDomain = host.replace(" ", "").split("[.]");
        return partDomain[partDomain.length - 2] + "." + partDomain[partDomain.length - 1];
    }

    public String getHostname() {
        return hostname;
    }

    public String getIP() {
        return IP;
    }
}