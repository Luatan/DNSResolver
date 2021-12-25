package Model.DNS;

import Model.DNS.Records.*;
import Utils.Domain;

import javax.naming.NameNotFoundException;
import javax.naming.OperationNotSupportedException;
import javax.naming.ServiceUnavailableException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class DnsAdapter {
    private String hostname;
    private final List<Record> records;
    public static final String[] RECORD_TYPES = {"A", "AAAA", "CNAME", "MX", "SRV", "TXT", "SOA"};

    public DnsAdapter(String domain, String type) {
        records = new LinkedList<>();
        setHost(domain);
        setNameServer();
        if (!type.equals("NS")){
            setRecords(type);
        }
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
        for (String record : RECORD_TYPES) {
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
                    //System.err.println("No Records for " + type + " in " + hostname + " found!");
                }
            }
        } catch (NameNotFoundException e) {
            addMessage("No DNS-Records found for " + hostname);
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
                record = record.replaceAll("\"", "");
                records.add(new TXT(record));
                break;
            case "NS":
                records.add(new NS(record));
                break;
            case "SRV":
                records.add(new SRV(record));
                break;
            case "SOA":
                int max;
                int padding = 2;

                List<String> list = Arrays.asList(record.split(" "));
                max = Collections.max(list).length();

                String formatting = "%-" + (max + padding) + "." + (max + padding) + "s" + "%s";

                list.set(1, list.get(1).replaceFirst("[.]", "@"));
                list.set(2, String.format(formatting, list.get(2), "serialnumber"));
                list.set(3, String.format(formatting, list.get(3), "refresh (" + Domain.getTimeFromSeconds(Integer.parseInt(list.get(3))) + ")"));
                list.set(4, String.format(formatting, list.get(4), "retry (" + Domain.getTimeFromSeconds(Integer.parseInt(list.get(4))) + ")"));
                list.set(5, String.format(formatting, list.get(5), "expire (" + Domain.getTimeFromSeconds(Integer.parseInt(list.get(5))) + ")"));
                list.set(6, String.format(formatting, list.get(6), "minimum (" + Domain.getTimeFromSeconds(Integer.parseInt(list.get(6))) + ")"));

                for (String rec : list) {
                    records.add(new SOA(rec));
                }

                break;
            default:
                System.err.println("No Record met case!");
                break;
        }
    }

    private void addMessage(String message) {
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getType().equals("MSG")){
                records.remove(i);
            }
        }
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