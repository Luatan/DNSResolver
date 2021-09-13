import Utils.Domain;
import Model.NIC;
import Model.Whois;
import javafx.concurrent.Task;

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
        if (Domain.isSubdomain(host)) {
            host = Domain.getMainDomain(host);
        }
        switch (Domain.getExtension(host)) {
            case "ag":
                updateValue(setDomainCheckResult(host, "whois.nic.ag"));
                break;
            case "shop":
                updateValue(setDomainCheckResult(host, "whois.nic.shop"));
                break;
            case "online":
                updateValue(setDomainCheckResult(host, "whois.nic.online"));
                break;
            case "gmbh":
                updateValue(setDomainCheckResult(host, "whois.nic.gmbh"));
                break;
            case "info":
                updateValue(setDomainCheckResult(host, "whois.afilias.net"));
                break;
            case "biz":
                updateValue(setDomainCheckResult(host, "whois.nic.biz"));
                break;
            case "ru":
                updateValue(setDomainCheckResult(host, "whois.tcinet.ru"));
                break;
            case "com":
            case "net":
                updateValue(setDomainCheckResult(host, "whois.verisign-grs.com"));
                break;
            case "org":
                updateValue(setDomainCheckResult(host, "whois.pir.org"));
                break;
            case "ca":
                updateMessage("whois.com/whois/" + host);
                updateValue("");
                break;
            case "eu":
                updateValue(setDomainCheckResult(host, "whois.eu"));
                break;
            case "fr":
                updateValue(setDomainCheckResult(host, "whois.afnic.fr"));
                break;
            case "ch":
            case "li":
                updateValue(getWHOIS_NIC(host));
                break;
            case "lu":
                updateValue(setDomainCheckResult(host, "whois.dns.lu"));
                break;
            case "swiss":
                updateValue(setDomainCheckResult(host, "whois.nic.swiss"));
                break;
            case "de":
                updateValue(setDomainCheckResult("-T dn " + host, "whois.denic.de"));
                break;
            case "at":
                updateValue(setDomainCheckResult(host, "whois.nic.at"));
                break;
            case "dk":
                updateValue(setDomainCheckResult(host, "whois.dk-hostmaster.dk"));
                break;
            case "nl":
                updateValue(setDomainCheckResult(host, "whois.domain-registry.nl"));
                break;
            case "uk":
                updateValue(setDomainCheckResult(host, "whois.nic.uk"));
                break;
            default:
                updateMessage("show Whois for " + host);
                updateValue("This TLD is not compatible");
                break;
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
