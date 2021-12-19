package ch.simon.Tasks;

import ch.simon.Model.DNS.DnsAdapter;
import ch.simon.Model.DNS.Records.Record;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;

public class DnsTask extends Task<List<String>> {
    long startTime = System.currentTimeMillis();
    private String host;
    private String type = "*";
    private List<String> result;
    private boolean showEmpty = false;
    private ObservableList<Record> nameservers;

    public DnsTask(String host) {
        this.host = host;
    }

    public DnsTask(String host, String type) {
        this(host);
        setType(type);
    }

    public void showEmpty(boolean show) {
        this.showEmpty = show;
    }

    public ObservableList<Record> getNameservers() {
        return nameservers;
    }


    private void setType(String type) {
        if (type.equals("Any")) {
            this.type = "*";
        } else {
            this.type = type;
        }
    }

    @Override
    protected List<String> call() throws Exception {
        result = new ArrayList<>();
        DnsAdapter query = new DnsAdapter(host, type);

        recordPutter(query.getRecords("MSG"), "MSG");
        if (type.equals("*")) {
            for (String request : DnsAdapter.RECORD_TYPES) {
                recordPutter(query.getRecords(request), request);
            }
        } else {
            recordPutter(query.getRecords(type), type);
        }

        //set Nameservers to list
        nameservers = FXCollections.observableArrayList();
        nameservers.addAll(query.getRecords("NS"));

        //Calculate Time for a request
        System.out.println("DNS Queries took: " + (System.currentTimeMillis() - startTime) + " ms");
        return result;
    }

    private void recordPutter(List<Record> list, String type) {
        if (!list.isEmpty()) {
            if (!type.equals("MSG")) {
                result.add(type + ": ");
            }
            for (Record rec : list) {
                result.add(rec.getValue());
            }
        } else if (showEmpty && !type.equals("MSG")) {
            result.add(type + ": ");
            result.add("No Records found");
        }
    }
}
