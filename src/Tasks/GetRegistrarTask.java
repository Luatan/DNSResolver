package Tasks;

import Model.API.NIC;
import Model.Whois;
import Utils.Config;
import Utils.Domain;
import Utils.FileStructure;
import Utils.WhoisCache;
import javafx.concurrent.Task;
import org.json.JSONObject;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetRegistrarTask extends Task<String> {
    private final StringBuilder LINKTEXT = new StringBuilder();
    private String host;
    private String res = "";
    private WhoisCache cache;


    public GetRegistrarTask(String host) {
        this.host = Domain.trimDomain(host);
    }

    @Override
    protected String call() {
        // Don't run if host is not set
        if (host == null) {
            return "";
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
                    res = cache.readCache();
                    setLINKTEXT(getRegistrarName() + " (cached)");
                    updateValue(res);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return getValue();
            }
        }


        String ext = Domain.getExtension(host);
        // Handles CH and LI Method of getting the whois (whois server is not available)
        if (ext.equals("ch") | ext.equals("li")) {
            try {
                updateValue(getWHOIS_NIC(host));
//                cache.writeCache(res);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return getValue();
        }

        // Read config file
        JSONObject readObj = new JSONObject(Objects.requireNonNull(FileStructure.readFile(Config.WHOIS_CONF_FILE)));
        if (readObj.has(ext)) {
            try {
                updateValue(setDomainCheckResult(readObj.getString(ext)));
//                cache.writeCache(res);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return getValue();
    }


    private String setDomainCheckResult(String whoisServer) {
        String host = this.host;
        // Handles DE Domains (special params for full info)
        if (Domain.getExtension(this.host).equals("de")) {
            host = "-T dn " + this.host;
        }

        res = new Whois().getWhois(host, whoisServer);
        setLINKTEXT(getRegistrarName());
        return res;
    }

    private String getWHOIS_NIC(String domain) {
        res = new NIC(domain).getOutput();
        setLINKTEXT(getRegistrarName());
        return res;
    }

    private void setLINKTEXT(String message) {
        LINKTEXT.append(this.host);
        if (message != null && !message.isEmpty()) {
            LINKTEXT.append(" - ").append(message);
        }
        updateMessage(LINKTEXT.toString());
    }

    private String getRegistrarName() {
        Pattern pattern = Pattern.compile("(Registrar:)([\\s].+)");
        Matcher matcher = pattern.matcher(res);

        if (matcher.find()) {
            return matcher.group(2).trim();
        }

        if (res.toLowerCase().contains("not found")) {
            return "Free";
        }

        if (res.toLowerCase().contains("status: free")){
            return "Free";
        }
        return null;
    }

}
