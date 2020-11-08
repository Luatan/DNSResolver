import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Requests {
    private String hostname;
    private String IP;
    private String[] A;
    private String[] AAAA;
    private String[] MX;
    private String[] NS;
    private String[] TXT;
    private String[] SRV;
    private String[] SOA;
    private String typeSet;

    Requests(String value, String records) throws UnknownHostException, NamingException {
        setHost(value);
        setRecords(value, records);
    }

    private void setHost(String host) {
        try {
            InetAddress inetHost = InetAddress.getByName(host);
            hostname = inetHost.getHostName();
            IP = inetHost.getHostAddress();

        } catch(UnknownHostException ex) {
            System.out.println("Unrecognized host");
            System.exit(1);
        }
    }

    private void setRecords(String host, String record) throws NamingException, UnknownHostException {
        typeSet = record;
        InetAddress inetAddress = InetAddress.getByName(host);
        InitialDirContext iDirC = new InitialDirContext();

        // get all the DNS records for inetAddress
        Attributes attributes = iDirC.getAttributes("dns:/" + inetAddress.getHostName(), new String[] {"*"});
        if(record.matches("[*]")) {
            System.out.println("This Feature is not yet implemented");
            System.exit(1);
        } else {
            try {
                String in = attributes.get(record).toString();
                //Get the Type
                String[] rec = in.split(":",2);
                String type = rec[0];
                //Get the Records
                String[] listRecords = in.split("(,)( )");
                String[] tempRecords = listRecords[0].split(": ");
                listRecords[0] = tempRecords[1];
                switch (type) {
                    case "*":

                        break;
                    case "A":
                        A = listRecords;
                        break;
                    case "AAAA":
                        AAAA = listRecords;
                        break;
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
                        System.out.println("Type was not found");
                        break;
                }
            } catch (Exception e) {
                System.out.println("Type was not found");
            }
        }
    }

    public String[] getRecords() {
        String type = typeSet;
        switch (type) {
            case "*":
                break;
            case "A":
                return A;
            case "AAAA":
                return AAAA;
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
                System.out.println("Type was not found");
                break;
        }
        return null;
    }

    public String getHostname() { return hostname; }
    public String getIP(){
        return IP;
    }
}
