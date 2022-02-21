package Model.Tasks;

import Model.DNS.DnsAdapter;
import Model.DNS.Records.Record;
import Model.Utils.DNSType;
import Model.Utils.SpecialType;
import Model.Utils.Type;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;

public class DnsTask extends Task<List<String>> {
    private final String host;
    private final String dnsServer;
    long startTime = System.currentTimeMillis();
    private Type type = SpecialType.ANY;
    private List<String> result;
    private boolean showEmpty = false;
    private ObservableList<Record> nameservers;

    public DnsTask(String host, String dnsServer) {
        this.host = host;
        this.dnsServer = dnsServer;
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
        result = new ArrayList<>();
        DnsAdapter query = new DnsAdapter(host, type, dnsServer);
        recordPutter(query.getRecords(SpecialType.MSG), SpecialType.MSG);
        if (type.equals(SpecialType.ANY)) {
            for (DNSType request : DnsAdapter.RECORD_TYPES) {
                recordPutter(query.getRecords(request), request);
            }
        } else {
            recordPutter(query.getRecords(type), type);
        }

        //set Nameservers to list
        nameservers = FXCollections.observableArrayList();
        nameservers.addAll(query.getRecords(SpecialType.NS));

        //Calculate Time for a request
        System.out.println("DNS Queries took: " + (System.currentTimeMillis() - startTime) + " ms");
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
}
