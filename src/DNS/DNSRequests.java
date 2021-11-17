package DNS;

import DNS.Records.*;
import Utils.Domain;

import javax.naming.NameNotFoundException;
import javax.naming.OperationNotSupportedException;
import javax.naming.ServiceUnavailableException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class DNSRequests {
    private String hostname;
    private final List<Record> records;

    public DNSRequests(String domain, String type) {
        long startTime = System.currentTimeMillis();
        records = new ArrayList<>();
        setHost(domain);
        setNameServer();
        setRecords(type);

        //Calculate Time for a request
        System.out.println("DNS Queries took: " + (System.currentTimeMillis() - startTime) + " ms");
    }

    private void setHost(String host) {
        this.hostname = Domain.trimDomain(host);
    }

    private void setNameServer() {
        if (Domain.isSubdomain(hostname)) {
            String origHost = hostname;
            this.hostname = Domain.getMainDomain(hostname);
            setRecords("NS");
            this.hostname = origHost;
        } else if (!Domain.isIPAdress(hostname)) {
            setRecords("NS");
        }
    }

    private void setAllRecords() {
        String[] recordsToUse = {"A", "AAAA", "CNAME", "MX", "SRV", "TXT", "SOA"};
        for (String record : recordsToUse) {
            setRecords(record);
        }
    }

    private void setRecords(String type) {
        if (type.matches("PTR")) {
            createRecord(getPTRRecord(hostname), type);
            return;
        }

        if (hostname == null || hostname.equals("")) {
            return;
        }

        try {
            // get all the DNS records for hostname
            Attributes attributes = new InitialDirContext().getAttributes("dns:/" + hostname, new String[]{type});
            if (type.matches("[*]")) {
                setAllRecords();
            } else {
                try {
                    //Get Attribute
                    Attribute attr = attributes.get(type);
                    for (int i = 0; i < attr.size(); i++) {
                        createRecord(attr.get(i).toString(), type);
                    }

                } catch (Exception e) {
                    //System.err.println("No DNS.Records for " + type + " in " + hostname + " found!");
                }
            }
        } catch (NameNotFoundException e) {
            addMessage("No DNS-DNS.Records Found for " + hostname);
        } catch (ServiceUnavailableException e) {
            addMessage("Service unavailable for " + hostname);
        } catch (OperationNotSupportedException e) {
            addMessage("could not resolve " + hostname);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createRecord(String record, String type) {
        switch (type) {
            case "MSG":
                records.add(new MSG(record));
                break;
            case "A":
                records.add(new A(record));
                break;
            case "AAAA":
                records.add(new AAAA(record));
                break;
            case "MX":
                records.add(new MX(record));
                break;
            case "CNAME":
                records.add(new CNAME(record));
                break;
            case "TXT":
                records.add(new TXT(record));
                break;
            case "NS":
                records.add(new NS(record));
                break;
            case "SOA":
                records.add(new SOA(record));
                break;
            default:
                System.err.println("No Record met case!");
                break;
        }
    }

    private void addMessage(String message) {
        System.err.println("Error: " + message);
        createRecord(message, "MSG");
    }

    private String getPTRRecord(String host) {
        try {
            InetAddress inetHost = InetAddress.getByName(host);
            if (Domain.isIPAdress(inetHost.getCanonicalHostName())) {
                return "No PTR Record found!";
            } else {
                return inetHost.getCanonicalHostName();
            }
        } catch (UnknownHostException e) {
            System.out.println("No PTR Record found");
        }

        return null;
    }

    public List<Record> getRecords(String type) {
        List<Record> list = new ArrayList<>();
        for (Record record : records) {
            if (record.getType().equals(type)) {
                list.add(record);
            }
        }
        return list;
    }
}