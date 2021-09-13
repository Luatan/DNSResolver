import Model.NIC;
import Model.Whois;
import Utils.Domain;
import Utils.FileStructure;
import javafx.concurrent.Task;
import org.json.JSONObject;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetRegistrarTask extends Task<String> {
    private final String CONF_FILE = "config/whois_servers.json";
    private String host;
    private StringBuilder message = new StringBuilder("Show Whois for ");
    private String res = "";


    GetRegistrarTask(String host) {
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

        // Handles DE Domains (special params for full info)
        String ext = Domain.getExtension(host);
        if (ext.equals("de")) {
            host = "-T dn " + host;
        }

        // Handles CH and LI Method of getting the whois (whois server is not available)
        if (ext.equals("ch") | ext.equals("li")) {
            updateValue(getWHOIS_NIC(host));
            return String.valueOf(valueProperty());
        }

        //Checks if Configuration File is in Place
        if (!FileStructure.fileExists(CONF_FILE)){
            FileStructure.createFile("config/default_whois.json", CONF_FILE);
        }

        // Read config file
        JSONObject readObj = new JSONObject(Objects.requireNonNull(FileStructure.readFile(CONF_FILE)));
        if (readObj.has(ext)) {
            updateValue(setDomainCheckResult(host, readObj.getString(ext)));
        } else {
            updateMessage("show Whois for " + host);
            updateValue("This TLD is not compatible");
        }

        return String.valueOf(valueProperty());
    }


    private String setDomainCheckResult(String host, String whoisServer) {
        res = new Whois().getWhois(host, whoisServer);
        message.append(this.host);
        String reg = getRegistrarName();
        if (reg != null) {
            message.append(" - " + reg);
        }
        updateMessage(message.toString());

        return res;
    }

    private String getWHOIS_NIC(String domain) {
        res = new NIC(domain).getNicValues();
        message.append(this.host);

        String reg = getRegistrarName();
        if (reg != null) {
            message.append(" - " + reg);
        }
        updateMessage(message.toString());

        return res;
    }

    private String getRegistrarName() {
        Pattern pattern = Pattern.compile("(?:Registrar:) (.+)");
        Matcher matcher = pattern.matcher(res);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

}
