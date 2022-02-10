package Model.Utils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Domain {

    public static boolean isSubdomain(String host) {
        return host.split("[.]", 3).length > 2 && !isIPAdress(host);
    }

    public static boolean isIPAdress(String host) {
        Matcher v4 = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$").matcher(host);
        Matcher v6 = Pattern.compile("^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$").matcher(host);
        return v4.find() || v6.find();
    }

    public static String getTimeFromSeconds(int seconds) {
        int days, hours, mins;

        mins = (seconds - seconds % 60) / 60;
        hours = (mins - mins % 60) / 60;
        mins -= mins - mins % 60;
        days = (hours - hours % 24) / 24;
        hours -= hours - hours % 24;

        return ((days > 0) ? days + " days" : "") + ((hours > 0) ? hours + " hours" : "") + ((hours > 0 && mins > 0) ? " " : "") + ((mins > 0) ? mins + " mins" : "");
    }

    public static String getMainDomain(String hostname) {
        String[] host = hostname.trim().split("[.]");
        String extension = getExtension(hostname);
        return host[host.length - (extension.substring(1).split("[.]").length + 1)] + extension;
    }

    public static String getExtension(String hostname) {
        String[] host = hostname.trim().split("[.]");
        if (host.length > 2) {
            StringTokenizer tokenizer = new StringTokenizer(getCcTLD(), ",");
            String ccTLD = "." + host[host.length - 2] + "." + host[host.length - 1];
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken().trim();
                if (ccTLD.equalsIgnoreCase(token)) {
                    return token;
                }
            }
        }
        return "." + host[host.length - 1].toLowerCase();
    }

    public static String getCcTLD() {
        StringBuilder tlds = new StringBuilder();
        try (InputStreamReader streamReader =
                     new InputStreamReader(Objects.requireNonNull(Domain.class.getClassLoader().getResourceAsStream("assets/ccTLD.txt")), StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                tlds.append(line).append(",");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return tlds.toString();
    }

    public static String trimDomain(String domain) {
        return java.net.IDN.toASCII(domain.toLowerCase().trim());
    }
}
