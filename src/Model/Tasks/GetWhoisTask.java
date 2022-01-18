package Model.Tasks;

import Caching.WhoisDataCache;
import Model.API.NIC;
import Model.Utils.*;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetWhoisTask extends Task<List<String>> {
    private final StringBuilder linkText = new StringBuilder();
    private String host;
    private List<String> res;
    private WhoisDataCache cache;


    public GetWhoisTask(String host) {
        this.host = Domain.trimDomain(host);
    }

    @Override
    protected List<String> call() {
        res = new ArrayList<>();
        // Don't run if host is not set
        if (host == null) {
            return res;
        }

        // Checks if the domain is the maindomain
        if (Domain.isSubdomain(host)) {
            host = Domain.getMainDomain(host);
        }

        // Check if this whois is cached
        if (Config.CACHING) {
            cache = new WhoisDataCache(host);

            if (cache.isCached()) {
                try {
                    res = cache.readLines();

                    setLinkText(" (cached)");
                    updateValue(res);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return res;
            }
        }

        String ext = Domain.getExtension(host);
        // Handles CH and LI Method of getting the whois (whois server is not available)
        if (ext.equals("ch") | ext.equals("li")) {
            try {
                updateValue(getWHOIS_NIC(host));
                if (Config.CACHING) {
                    cache.writeCache(res);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
        }

        //get Server
        WhoisServerSearch serverSearch = new WhoisServerSearch();
        WhoisServer server = serverSearch.search(ext);
        if (server == null) {
            return res;
        }
        //update the value
        updateValue(setDomainCheckResult(server.getWhois()));
        if (Config.CACHING) {
            cache.writeCache(res);
        }
        return res;
    }

    private List<String> setDomainCheckResult(String whoisServer) {
        String host = this.host;
        // Handles DE Domains (special params for full info)
        if (Domain.getExtension(this.host).equals("de")) {
            host = "-T dn " + this.host;
        }

        res = new Whois().getWhois(host, whoisServer);
        setLINKTEXT();

        return res;
    }

    private List<String> getWHOIS_NIC(String domain) {
        res = new NIC(domain).getOutput();
        setLINKTEXT();
        return res;
    }

    private void setLINKTEXT() {
        linkText.append(this.host);
        String registrar = searchWhois("(?:registrar[:\\n]|registrar-name[:])[\\W\\r]+(?:Organization:)?(?:[\\W\\r]+)?(.+)");
        if (registrar.length() > 0) {
            linkText.append(" - ").append(registrar);
        }
        updateMessage(linkText.toString());
    }

    private void setLinkText(String message) {
        setLINKTEXT();
        if (message != null && !message.isEmpty()) {
            linkText.append(message);
        }

        updateMessage(linkText.toString());
    }

    private String searchWhois(String regex) {
        Pattern input = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Pattern de = Pattern.compile("(?:Status:)\\W(connect)", Pattern.CASE_INSENSITIVE);

        if (res.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (String line : res) {
            sb.append(line).append("\n");
        }

        Matcher matcher = input.matcher(sb.toString());
        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        matcher = de.matcher(sb.toString());
        if (matcher.find()) {
            return "Registred";
        }


        return "Free";
    }

}
