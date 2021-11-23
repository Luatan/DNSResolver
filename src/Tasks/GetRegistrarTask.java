package Tasks;

import Model.API.NIC;
import Model.Whois;
import Utils.Config;
import Utils.Domain;
import Utils.FileStructure;
import javafx.concurrent.Task;
import org.json.JSONObject;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetRegistrarTask extends Task<String> {
    private final StringBuilder MESSAGE = new StringBuilder();
    private String host;
    private String res = "";


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

        // Handles DE Domains (special params for full info)
        String ext = Domain.getExtension(host);
        if (ext.equals("de")) {
            host = "-T dn " + host;
        }

        // Handles CH and LI Method of getting the whois (whois server is not available)
        if (ext.equals("ch") | ext.equals("li")) {
            try {
                updateValue(getWHOIS_NIC(host));
            } catch (Exception e){
                e.printStackTrace();
            }
            return String.valueOf(valueProperty());
        }

        // Read config file
        JSONObject readObj = new JSONObject(Objects.requireNonNull(FileStructure.readFile(Config.WHOIS_CONF_FILE)));
        if (readObj.has(ext)) {
            try {
                updateValue(setDomainCheckResult(host, readObj.getString(ext)));
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return String.valueOf(valueProperty());
    }


    private String setDomainCheckResult(String host, String whoisServer) {
        res = new Whois().getWhois(host, whoisServer);
        MESSAGE.append(this.host);
        String reg = getRegistrarName();
        if (reg != null) {
            MESSAGE.append(" - ").append(reg);
        }
        updateMessage(MESSAGE.toString());

        return res;
    }

    private String getWHOIS_NIC(String domain) {
        res = new NIC(domain).getOutput();
        MESSAGE.append(this.host);

        String reg = getRegistrarName();
        if (reg != null) {
            MESSAGE.append(" - ").append(reg);
        }
        updateMessage(MESSAGE.toString());

        return res;
    }

    private String getRegistrarName() {
        Pattern pattern = Pattern.compile("(Registrar:)([\\s].+)");
        Matcher matcher = pattern.matcher(res);

        if (matcher.find()) {
            return matcher.group(2).trim();
        }
        return null;
    }

}
