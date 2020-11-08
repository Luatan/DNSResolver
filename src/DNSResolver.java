import javax.naming.NamingException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class DNSResolver {

    public static void main(String[] args) throws NamingException, UnknownHostException {


    }

    public static void Resolver() throws NamingException, UnknownHostException {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter a Domain: ");
        String host = input.next();

        System.out.println("Enter the desired Records");
        String record = input.next();

        Requests mySearch = new Requests(host, record);
        System.out.println(mySearch.getHostname());
        System.out.println(mySearch.getIP());
        System.out.println();

        for (String element: mySearch.getRecords()) {
            System.out.println(element);
        }
    }
}