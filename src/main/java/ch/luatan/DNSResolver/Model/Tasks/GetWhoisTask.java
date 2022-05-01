package ch.luatan.DNSResolver.Model.Tasks;


import ch.luatan.DNSResolver.Data.API.API;
import ch.luatan.DNSResolver.Data.API.Nic;
import ch.luatan.DNSResolver.Model.Caching.WhoisDataCache;
import ch.luatan.DNSResolver.Model.Utils.Config;
import ch.luatan.DNSResolver.Model.Utils.Domain;
import ch.luatan.DNSResolver.Data.Whois.Whois;
import ch.luatan.DNSResolver.Model.Whois.WhoisServer;
import ch.luatan.DNSResolver.Model.Whois.WhoisServerSearch;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetWhoisTask extends Task<List<String>> {
    private final StringBuilder linkText = new StringBuilder();
    private final List<String> regexPreambles = new ArrayList<>(Arrays.asList("registrar", "registrar-name", "organization"));
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

                    //set cached sign
                    setLinkRegistrar();
                    linkText.append(" (cached)");
                    updateMessage(linkText.toString());

                    updateValue(res);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return res;
            }
        }

        String ext = Domain.getExtension(host);
        //choose between implementations of the Whois API depending on domain extension
        API api;
        if (ext.equals(".ch") | ext.equals(".li")) {
            api = new Nic();
        } else {
            //get Whoisserver from IANNA
            WhoisServerSearch serverSearch = new WhoisServerSearch();
            WhoisServer server = serverSearch.search(ext);
            if (server == null) {
                return res;
            }
            api = new Whois(server.getWhois());

        }
        res = api.query(host);

        // displays the registrar of the domain in the link
        setLinkRegistrar();

        if (Config.CACHING) {
            cache.writeCache(res);
        }
        return res;
    }

    private void setLinkRegistrar() {
        linkText.append(this.host);
        String registrar = searchWhois(buildRegex());
        if (registrar.length() > 0) {
            linkText.append(" - ").append(registrar);
        }
        updateMessage(linkText.toString());
    }

    private String buildRegex() {
        // join all possible staring Strings to a preamble
        String preamble = "(?:" + String.join("|", regexPreambles) + ")";
        //handles spaces and stuff, also gets the result in the registrar group
        String regexValue = "(?:[:\\n])(?:[:\\n\\W\\r]+)?(?<registrar>.+)";
        return "^" + preamble + regexValue;
    }

    private String searchWhois(String regex) {
        Pattern input = Pattern.compile(regex, Pattern.CASE_INSENSITIVE + Pattern.MULTILINE);
        Pattern de = Pattern.compile("(?:Status:)\\W(connect)", Pattern.CASE_INSENSITIVE + Pattern.MULTILINE);

        if (res.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (String line : res) {
            sb.append(line.trim()).append("\n");
        }

        Matcher matcher = input.matcher(sb.toString());
        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        matcher = de.matcher(sb.toString());
        if (matcher.find()) {
            return "Registred";
        }
        return "";
    }

}
