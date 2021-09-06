import Utils.Domain;
import Model.NIC;
import Model.Whois;
import javafx.concurrent.Task;

public class GetRegistrarTask extends Task<String> {
    private String host;

    GetRegistrarTask(String host) {
        this.host = Domain.trimDomain(host);
    }

    @Override
    protected String call() throws Exception {
        if (Domain.isSubdomain(host)) {
            host = Domain.getMainDomain(host);
        }
        switch (Domain.getExtension(host)) {
            case "com":
            case "net":
            case "ru":
            case "org":
                updateValue(setDomainCheckResult(host, "whois.psi-usa.info"));
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
        updateMessage("show Whois for " + this.host);
        return new Whois().getWhois(host, whoisServer);
    }

    private String getWHOIS_NIC(String domain) {
        NIC api = new NIC(domain);
        updateMessage("show Whois for " + this.host);
        return api.getNicValues();
    }
}
