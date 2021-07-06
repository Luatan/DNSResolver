import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Domain {

    public static boolean isSubdomain(String host) {
        return host.split("[.]", 3).length > 2 && !isIPAdress(host);
    }

    public static boolean isIPAdress(String host) {
        Matcher m = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$").matcher(host);
        return m.find();
    }

    public static String getMainDomain(String host) {
        String[] partDomain = host.replace(" ", "").split("[.]");
        return partDomain[partDomain.length - 2] + "." + partDomain[partDomain.length - 1].toLowerCase();
    }

    public static String getTimeFromSeconds(int time) {
        int days, hours, mins;

        mins = (time - time%60)/60;
        hours = (mins - mins%60)/60;
        mins -= mins - mins%60;
        days = (hours - hours%24)/24;
        hours -= hours - hours%24;

        return ((days > 0) ? days + " days" : "") + ((hours > 0) ? hours + " hours" : "") + ((hours > 0 && mins > 0) ? " " : "") + ((mins > 0) ? mins + " mins" : "");
    }

    public static String getExtension(String hostname) {
        String[] host = hostname.split("[.]");
        return host[host.length - 1].toLowerCase();
    }

    public static String trimDomain(String domain){
        return java.net.IDN.toASCII(domain.toLowerCase().trim());
    }
}
