import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class Requests {
    private String hostname;
    private String IP;
    private String[] ANY;
    private String[] A;
    private String[] AAAA;
    private String[] MX;
    private String[] NS;
    private String[] TXT;
    private String[] SRV;
    private String[] SOA;
    private String typeSet;

    Requests(String value, String type) throws UnknownHostException, NamingException {
        setHost(value);
        setRecords(type);
    }

    private void setHost(String host) {
        try {
            InetAddress inetHost = InetAddress.getByName(host);
            hostname = inetHost.getHostName();
            IP = inetHost.getHostAddress();

        } catch(UnknownHostException ex) {
            hostname = "Unrecognized host";
            System.out.println("Unrecognized host");
        }
    }

    private void setAllRecords(String hostname) throws NamingException, UnknownHostException {
        setRecords("A");
        setRecords("AAAA");
        setRecords("MX");
        setRecords("NS");
        setRecords("TXT");
        setRecords("SRV");
        setRecords("SOA");

        System.out.println(Arrays.toString(MX));
    }

    private void setRecords(String record) throws NamingException, UnknownHostException {
        typeSet = record;

        InitialDirContext iDirC = new InitialDirContext();
        // get all the DNS records for hostname
        Attributes attributes = iDirC.getAttributes("dns:/" + hostname, new String[] {"*"});

        if(record.matches("[*]")) {
            typeSet = "*";
            setAllRecords(hostname);

        } else {
            try {
                //Get Type
                String type = attributes.get(record).toString().split(":",2)[0];
                //Get the Records
                String[] listRecords = attributes.get(record).toString().split("(,)( )");
                String[] tempRecords = listRecords[0].split(": ");
                //Replace first char with the actual value instead of the type
                listRecords[0] = tempRecords[1];

                populateRecords(listRecords);

            } catch (Exception e) {
                System.out.println("No Records found");
            }
        }
    }

    private void populateRecords(String[] listRecords) {
        String type = typeSet;
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
                System.out.println("type not found");

                break;
        }
    }

    public String[] getRecords(String type) {
        switch (type) {
            case "*":
                return ANY;
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
    public String getIP(){ return IP; }
}
