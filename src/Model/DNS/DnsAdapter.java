package Model.DNS;

import Model.DNS.Records.Record;
import Model.Utils.Domain;
import Model.Utils.RecordTypes;

import javax.naming.*;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class DnsAdapter {
    public static final RecordTypes[] RECORD_TYPES = {RecordTypes.A, RecordTypes.AAAA, RecordTypes.CNAME,
            RecordTypes.MX, RecordTypes.SRV, RecordTypes.TXT, RecordTypes.SOA};
    private final List<Record> records;
    private InitialDirContext iDirC;
    private String hostname;

    public DnsAdapter(String domain, RecordTypes type, String dnsServer) {
        //set environment for nameresolution
        Hashtable<String, String> dnsEnv = new Hashtable<>();
        dnsEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.dns.DnsContextFactory");
        dnsEnv.put("com.sun.jndi.dns.timeout.initial", "2000");
        dnsEnv.put("com.sun.jndi.dns.timeout.retries", "1");

        //set DNS Server which will be queried. leave empty, if it should select automatically
        dnsEnv.put(Context.PROVIDER_URL, "dns://" + dnsServer);

        try {
            iDirC = new InitialDirContext(dnsEnv);
        } catch (NamingException e) {
            e.printStackTrace();
        }

        records = new LinkedList<>();
        setHost(domain);
        setNameServer();
        if (!type.equals(RecordTypes.NS)) {
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
            setRecords(RecordTypes.NS);
            this.hostname = origHost;
        } else if (!Domain.isIPAdress(hostname)) {
            setRecords(RecordTypes.NS);
        }
    }

    private void setAllRecords() {
        for (RecordTypes record : RECORD_TYPES) {
            setRecords(record);
        }
    }

    private void setRecords(RecordTypes recordType) {
        if (hostname == null || hostname.equals("")) {
            return;
        }

        try {
            // get all the DNS records for hostname
            Attributes attributes = iDirC.getAttributes(hostname, new String[]{recordType.toString()});
            if (recordType.equals(RecordTypes.ANY)) {
                setAllRecords();
            } else {
                try {
                    //Get Attribute
                    Attribute attr = attributes.get(recordType.toString());
                    for (int i = 0; i < attr.size(); i++) {
                        createRecord(attr.get(i).toString(), recordType);
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
        } catch (CommunicationException e) {
            addMessage(e.getMessage() + " - timeout");
        } catch (ConfigurationException e) {
            addMessage(e.getMessage());
        } catch (Exception e) {
            addMessage("an Unknown Error occured");
            e.printStackTrace();
        }
    }

    private void createRecord(String record, RecordTypes type) {
        if (type.equals(RecordTypes.TXT)) {
            record = record.replaceAll("\"", "");
        }

        if (type.equals(RecordTypes.SOA)) {
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
                records.add(Record.setRecord(Record.SOA, rec));
            }
        } else {
            records.add(Record.setRecord(type, record));
        }
    }

    private void addMessage(String message) {
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getType().equals("MSG")) {
                records.remove(i);
            }
        }
        System.err.println("Error: " + message);
        createRecord(message, Record.MSG);
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