import Utils.Domain;

import javax.naming.*;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class DNSRequests {
    private String hostname;
    private String ip;
    private String[] messages;
    private String[] a;
    private String[] aaaa;
    private String[] cname;
    private String[] mx;
    private String[] ns;
    private String[] txt;
    private String[] srv;
    private String[] soa;

    DNSRequests(String domain, String type) {
        setHost(Domain.trimDomain(domain));
        setRecords(type);
    }

    private void setHost(String host) {
        this.hostname = host;
//        try {
//            InetAddress inetHost = InetAddress.getByName(host);
//            hostname = inetHost.getHostName();
//            ip = inetHost.getHostAddress();
//        } catch (UnknownHostException ex) {
//            hostname = host;
//            System.err.println("This host: " + host + " has no IP Address");
//        }
        setNameServer();
    }

    private void setNameServer(){
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
            //If PTR-Record do not call the Model.DNS again - UI calls getPTRRecords Method
        } else if (!hostname.equals("Unrecognized host")) {
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
                addMessage("No DNS-Records Found for " + hostname);
            } catch (ServiceUnavailableException e) {
                addMessage("Service unavailable for " + hostname);
            } catch (OperationNotSupportedException e) {
                addMessage("could not resolve " + hostname);
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    public String[] getRecords(String type) {
        switch (type) {
            case "A":
                return a;
            case "AAAA":
                return aaaa;
            case "CNAME":
                return cname;
            case "MX":
                return mx;
            case "SOA":
                return (soa == null) ? null : formatSOA(soa);
            case "NS":
                return ns;
            case "SRV":
                return srv;
            case "TXT":
                return txt;
            case "PTR":
                return new String[]{getPTRRecord(hostname)};
            case "Messages":
                return messages;
            default:
                System.err.println("Type was not found - getRecords");
                break;
        }
        return null;
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

    public String getHostname() {
        return hostname.toLowerCase();
    }

    public String getIP() {
        return ip;
    }
}