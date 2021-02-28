import javafx.concurrent.Task;

public class GetRegistrarTask extends Task<String> {
    private String host;

    GetRegistrarTask(String host) {
        this.host = host;
    }

    @Override
    protected String call() throws Exception {
        DNSRequests query = new DNSRequests();
        if (query.isSubdomain(host)) {
            host = query.getMainDomain(host);
        }
        switch (query.getExtension(host)) {
            case "com":
            case "net":
            case "ru":
            case "org":
            case "ca":
                //hyperLbl = true;
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
                updateValue(setDomainCheckResultAPI("https://rdap.nic.ch/domain/", host));
                break;
            case "swiss":
                updateValue(setDomainCheckResult(host, "whois.nic.swiss"));
                break;
            case "de":
                updateValue(setDomainCheckResult("-T dn " + host, "whois.denic.de"));
                break;
            default:
                updateMessage("show Whois for " + host);
                //hyperLbl = true;
                updateValue("This TLD is not compatible");
                break;
        }
        return String.valueOf(valueProperty());
    }

    private String setDomainCheckResult(String host, String whoisServer) {
        updateMessage("show Whois for " + this.host);
        //hyperLbl = true;
        return new Whois().getWhois(host, whoisServer);
    }

    private String setDomainCheckResultAPI(String URL, String domain) {
        API api = new API(URL, domain);
        updateMessage("show Whois for " + this.host);
        //hyperLbl = true;
        return api.getNicValues();
    }
}
