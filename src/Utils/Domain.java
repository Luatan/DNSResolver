package Utils;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Domain {

    public static boolean isSubdomain(String host) {
        return host.split("[.]", 3).length > 2 && !isIPAdress(host);
    }

    public static boolean isIPAdress(String host) {
        Matcher v4 = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$").matcher(host);
        Matcher v6 = Pattern.compile("^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$").matcher(host);
        if (v4.find() || v6.find()) {
            return true;
        }
        return false;
    }


    public static String getMainDomain(String host) {
        String[] partDomain = host.replace(" ", "").split("[.]");
        return partDomain[partDomain.length - 2] + "." + partDomain[partDomain.length - 1].toLowerCase();
    }

    public static String getTimeFromSeconds(int seconds) {
        int days, hours, mins;

        mins = (seconds - seconds%60)/60;
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
