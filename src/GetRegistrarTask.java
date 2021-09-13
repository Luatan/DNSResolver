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
    private String host;
    private StringBuilder message = new StringBuilder("Show Whois for ");
    private String res = "";


    GetRegistrarTask(String host) {
        this.host = Domain.trimDomain(host);
    }

    @Override
    protected String call() {
        if (host == null) {
            return "";
        }

        if (Domain.isSubdomain(host)) {
            host = Domain.getMainDomain(host);
        }

        String ext = Domain.getExtension(host);
        if (ext.equals("de")) {
            host = "-T dn " + host;
        }

        if (ext.equals("ch") | ext.equals("li")) {
            updateValue(getWHOIS_NIC(host));
            return String.valueOf(valueProperty());
        }

        JSONObject readObj = new JSONObject(Objects.requireNonNull(FileStructure.readFile("config/whois_servers.json")));
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
