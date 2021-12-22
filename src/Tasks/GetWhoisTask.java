package Tasks;

import Model.API.NIC;
import Model.JsonAdapter;
import Model.Whois;
import Utils.Config;
import Utils.Domain;
import Utils.FileStructure;
import Utils.WhoisCache;
import com.google.gson.reflect.TypeToken;
import javafx.concurrent.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetWhoisTask extends Task<List<String>> {
    private final StringBuilder LINKTEXT = new StringBuilder();
    private String host;
    private List<String> res;
    private WhoisCache cache;


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
            cache = new WhoisCache(host);

            if (cache.isCached()) {
                try {
                    res = cache.readCacheByLine();

                    setLINKTEXT(" (cached)");
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

        // Read config file
        Map<String, String> whois_list;
        try {
            whois_list = JsonAdapter.HANDLER.fromJson(FileStructure.getReader(Config.WHOIS_CONF_FILE), new TypeToken<Map<String, String>>() {
            }.getType());

            if (whois_list.containsKey(ext)) {
                try {
                    updateValue(setDomainCheckResult(whois_list.get(ext)));
                    if (Config.CACHING) {
                        cache.writeCache(res);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return res;
        } catch (IOException e) {
            e.printStackTrace();
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
        LINKTEXT.append(this.host);
        String registrar = searchWhois("(?:registrar:)\\W(.+)");
        if (registrar.length() > 0) {
            LINKTEXT.append(" - ").append(registrar);
        }
        updateMessage(LINKTEXT.toString());
    }

    private void setLINKTEXT(String message) {
        setLINKTEXT();
        if (message != null && !message.isEmpty()) {
            LINKTEXT.append(message);
        }

        updateMessage(LINKTEXT.toString());
    }

    private String searchWhois(String regex) {

        if (res.isEmpty()) {
            return "";
        }

        for (String line:res) {
            Matcher matcher = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(line);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        }
        return "Free";
    }

}
