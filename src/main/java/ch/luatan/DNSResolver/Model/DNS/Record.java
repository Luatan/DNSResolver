package ch.luatan.DNSResolver.Model.DNS;

public class Record {
    protected Type type;
    protected String value;

    public static Record setRecordValue(Record emptyRecord, String value) {
        emptyRecord.setValue(value);
        return emptyRecord;
    }

    public Record(Type type) {
        setType(type);
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    protected void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Record{" +
                "type=" + type +
                ", value='" + value + '\'' +
                '}';
    }
}
