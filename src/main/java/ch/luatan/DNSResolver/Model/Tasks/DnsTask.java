package ch.luatan.DNSResolver.Model.Tasks;

import ch.luatan.DNSResolver.DNSResolver;
import ch.luatan.DNSResolver.Data.Resolver.DNSJavaResolver;
import ch.luatan.DNSResolver.Data.Resolver.Resolvable;
import ch.luatan.DNSResolver.Model.DNS.*;
import ch.luatan.DNSResolver.Model.Utils.Domain;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;

public class DnsTask extends Task<List<String>> {
    private final Resolvable query;
    private final String host;
    private final String dnsServer;
    private final List<String> result;
    long startTime = System.currentTimeMillis();
    private Type type = SpecialType.ANY;
    private boolean showEmpty = false;
    private ObservableList<Record> nameservers;

    public DnsTask(String host, String dnsServer) {
        this.host = Domain.trimDomain(host);;
        this.dnsServer = dnsServer;
        query = new DNSJavaResolver();
        if (DNSResolver.isIgnoreDNSSEC() || !dnsServer.isEmpty()) {
            query.useDNSSEC(false);
        }
        result = new ArrayList<>();
    }

    public DnsTask(String host, Type type, String dnsServer) {
        this(host, dnsServer);
        this.type = type;
    }

    public void showEmpty(boolean show) {
        this.showEmpty = show;
    }

    public ObservableList<Record> getNameservers() {
        return nameservers;
    }

    @Override
    protected List<String> call() {
        query.resolve(host, type, dnsServer);
        recordPutter(query.getRecords(SpecialType.MSG), SpecialType.MSG);
        if (type.equals(SpecialType.ANY)) {
            for (DNSType request : Resolvable.RECORD_TYPES) {
                recordPutter(query.getRecords(request), request);
            }
        } else {
            recordPutter(query.getRecords(type), type);
        }

        //set Nameservers to list
        nameservers = FXCollections.observableArrayList();
        nameservers.addAll(query.getRecords(AdditionalTypes.NS));

        if (result.isEmpty()) {
            result.add("No Records found");
        }

        //Calculate Time for a request
        DNSResolver.LOGGER.debug("DNS Queries took: " + (System.currentTimeMillis() - startTime) + " ms");
        return result;
    }

    private void recordPutter(List<Record> list, Type type) {
        if (!list.isEmpty()) {
            if (!type.equals(SpecialType.MSG)) {
                result.add(type + ": ");
            }
            for (Record rec : list) {
                result.add(rec.getValue());
            }
        } else if (showEmpty && !type.equals(SpecialType.MSG)) {
            result.add(type + ": ");
            result.add("No Records found");
        }
    }

    public String seucreZone() {
        return query.validateDNSSEC();
    }
}
