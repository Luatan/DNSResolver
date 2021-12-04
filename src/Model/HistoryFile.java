package Model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class HistoryFile {
    LinkedHashSet<String> domains = new LinkedHashSet<>();

    public void addDomain(String domain) {
        if (domain.equals("")) {
            return;
        }

        if (domainExists(domain)) {
            domains.remove(domain);
        }

        domains.add(domain);

        if (domains.size() > 10) {
            domains.remove(domains.stream().findFirst().get());
        }
    }

    private boolean domainExists(String domain) {
        return domains.contains(domain);
    }

    public List<String> getDomains() {
        return new ArrayList<>(domains);
    }
}
