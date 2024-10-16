package ch.luatan.DNSResolver.Data.Resolver;

import ch.luatan.DNSResolver.DNSResolver;
import ch.luatan.DNSResolver.Model.DNS.*;
import ch.luatan.DNSResolver.Model.Utils.Domain;

import javax.naming.*;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import java.util.*;

public class DefaultResolver implements Resolvable {
    private final List<Record> records = new LinkedList<>();
    private InitialDirContext iDirC;
    private String hostname;

    public void resolve(String domain, Type type, String dnsServer) {
        this.hostname = domain;

        //set environment for nameresolution
        Hashtable<String, String> dnsEnv = new Hashtable<>();
        dnsEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.dns.DnsContextFactory");
        dnsEnv.put("com.sun.jndi.dns.timeout.initial", "2000");
        dnsEnv.put("com.sun.jndi.dns.timeout.retries", "1");

        //set DNS Server which will be queried. leave empty, if it should select automatically
        dnsEnv.put(Context.PROVIDER_URL, "dns://" + dnsServer);

        // Init Context
        try {
            iDirC = new InitialDirContext(dnsEnv);
        } catch (NamingException e) {
            e.printStackTrace();
        }

        setNameServer();
        if (!type.equals(AdditionalTypes.NS)) {
            setRecords(type);
        }
    }

    public List<Record> getRecords(Type type) {
        List<Record> list = new ArrayList<>();
        for (Record record : records) {
            if (record.getType().equals(type)) {
                list.add(record);
            }
        }
        return list;
    }

    @Override
    public String validateDNSSEC() {
        return "Not implemented!";
    }

    @Override
    public void useDNSSEC(boolean value) {
    }

    private void setNameServer() {
        if (Domain.isSubdomain(hostname)) {
            String origHost = hostname;
            this.hostname = Domain.getMainDomain(hostname);
            setRecords(AdditionalTypes.NS);
            this.hostname = origHost;
        } else if (!Domain.isIPAdress(hostname)) {
            setRecords(AdditionalTypes.NS);
        }
    }

    private void setAllRecords() {
        for (DNSType record : RECORD_TYPES) {
            setRecords(record);
        }
    }

    private void setRecords(Type type) {
        if (hostname == null || hostname.equals("")) {
            return;
        }
        try {
            // get all the DNS records for hostname
            Attributes attributes = iDirC.getAttributes(hostname, new String[]{(type.equals(SpecialType.ANY)) ? "*" : type.toString()});
            if (type.equals(SpecialType.ANY)) {
                setAllRecords();
            } else {
                try {
                    //Get Attribute
                    Attribute attr = attributes.get(type.toString());
                    for (int i = 0; i < attr.size(); i++) {
                        createRecord(attr.get(i).toString(), type);
                    }

                } catch (Exception e) {
                    //DNSResolver.LOGGER.error("No Records for " + type + " in " + hostname + " found!");
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

    private void createRecord(String value, Type type) {
        if (type.equals(DNSType.TXT)) {
            value = value.replaceAll("\"", "");
        }

        if (type.equals(DNSType.SOA)) {
            int max;
            int padding = 2;

            List<String> list = Arrays.asList(value.split(" "));
            max = Collections.max(list).length();

            String formatting = "%-" + (max + padding) + "." + (max + padding) + "s" + "%s";

            list.set(1, list.get(1).replaceFirst("[.]", "@"));
            list.set(2, String.format(formatting, list.get(2), "serialnumber"));
            list.set(3, String.format(formatting, list.get(3), "refresh (" + Domain.getTimeFromSeconds(Integer.parseInt(list.get(3))) + ")"));
            list.set(4, String.format(formatting, list.get(4), "retry (" + Domain.getTimeFromSeconds(Integer.parseInt(list.get(4))) + ")"));
            list.set(5, String.format(formatting, list.get(5), "expire (" + Domain.getTimeFromSeconds(Integer.parseInt(list.get(5))) + ")"));
            list.set(6, String.format(formatting, list.get(6), "minimum (" + Domain.getTimeFromSeconds(Integer.parseInt(list.get(6))) + ")"));

            for (String rec : list) {
                records.add(Record.setRecordValue(type.getRecord(), rec));
            }
        } else {
            Record recordToAdd = Record.setRecordValue(type.getRecord(), value);
            records.add(recordToAdd);
        }
    }

    private void addMessage(String message) {
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getType().equals(SpecialType.MSG)) {
                records.remove(i);
            }
        }
        DNSResolver.LOGGER.error(message);
        createRecord(message, SpecialType.MSG);
    }
}