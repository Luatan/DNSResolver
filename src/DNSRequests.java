import Records.*;
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
    private String[] messages;
    private String[] a;
    private String[] aaaa;
    private String[] cname;
    private String[] mx;
    private String[] ns;
    private String[] txt;
    private String[] srv;
    private String[] soa;
    private List<Record> records;

    DNSRequests(String domain, String type) {
        records = new ArrayList<>();
        setHost(domain);
        setNameServer();
        setRecords(type);
        System.out.println(records);
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
            return;
            //If PTR-Record do not call the DNS again - UI calls getPTRRecords Method
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

                    //Init String Array
                    String[] listRecords = new String[attr.size()];
                    for (int i = 0; i < attr.size(); i++) {
                        listRecords[i] = attr.get(i).toString();
                        createRecord(attr.get(i).toString(), type);

                    }

                    //Populate
                    populateRecords(listRecords, type);

                } catch (Exception e) {
                    //System.err.println("No Records for " + type + " in " + hostname + " found!");
                }
            }
        } catch (NameNotFoundException e) {
            addMessage("No DNS-Records Found for " + hostname);
        } catch (ServiceUnavailableException e) {
            addMessage("Service unavailable for " + hostname);
        } catch (OperationNotSupportedException e) {
            addMessage("could not resolve " + hostname);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createRecord(String record, String type) {
        switch (type){
            case "A":
                records.add(new A(type,record));
                break;
            case "AAAA":
                records.add(new AAAA(type,record));
                break;
            case "MX":
                records.add(new MX(type,record, 20));
                break;
            case "CNAME":
                records.add(new CNAME(type,record));
                break;
            case "TXT":
                records.add(new TXT(type,record));
                break;
            case "NS":
                records.add(new NS(type, record));
                break;
        }
    }

    private void addMessage(String message) {
        System.err.println(message);
        populateRecords(new String[]{message}, "Messages");
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

    private void populateRecords(String[] recordList, String type) {
        switch (type) {
            case "A":
                a = recordList;
                break;
            case "AAAA":
                aaaa = recordList;
                break;
            case "CNAME":
                cname = recordList;
                break;
            case "MX":
                mx = recordList;
                break;
            case "SOA":
                soa = recordList;
                break;
            case "NS":
                ns = recordList;
                break;
            case "TXT":
                txt = recordList;
                break;
            case "SRV":
                srv = recordList;
                break;
            case "Messages":
                messages = recordList;
                break;
            default:
                System.err.println("type was not found - PopulateRecords");
                break;
        }
    }

    public List<Record> getRecords(String type) {
        List<Record> list = new ArrayList<>();
        for (Record record:records) {
            if (record.getType().equals(type)){
                list.add(record);
            }
        }
        return list;
//        switch (type) {
//            case "A":
//                return a;
//            case "AAAA":
//                return aaaa;
//            case "CNAME":
//                return cname;
//            case "MX":
//                return mx;
//            case "SOA":
//                return (soa == null) ? null : formatSOA(soa);
//            case "NS":
//                return ns;
//            case "SRV":
//                return srv;
//            case "TXT":
//                return txt;
//            case "PTR":
//                return new String[]{getPTRRecord(hostname)};
//            case "Messages":
//                return messages;
//            default:
//                System.err.println("Type was not found - getRecords");
//                break;
//        }

    }

    private String[] formatSOA(String[] list) {
        String[] new_list = list[0].split(" ");

        new_list[2] += "\t\t serialnumber";
        new_list[3] += "\t\t\t\t refresh (" + Domain.getTimeFromSeconds(Integer.parseInt(new_list[3])) + ")";
        new_list[4] += "\t\t\t\t retry (" + Domain.getTimeFromSeconds(Integer.parseInt(new_list[4])) + ")";
        new_list[5] += "\t\t\t expire (" + Domain.getTimeFromSeconds(Integer.parseInt(new_list[5])) + ")";
        new_list[6] += "\t\t\t minimum (" + Domain.getTimeFromSeconds(Integer.parseInt(new_list[6])) + ")";

        return new_list;
    }
}