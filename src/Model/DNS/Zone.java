package Model.DNS;

import java.util.*;

public class Zone {
    private String domainName;
    private List<Record> records;

    public Zone() {
        records = new LinkedList<>();
    }

    public void addRecord(Record record) {
        records.add(record);
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public List<Record> getRecords(String type) {
        List<Record> rec = new ArrayList<>();
        for (Record record:records) {
            if (record.getType().equals(type)) {
                rec.add(record);
            }
        }
        return rec;
    }

    @Override
    public String toString() {
        return "Zone{" +
                "records=" + records +
                '}';
    }
}
