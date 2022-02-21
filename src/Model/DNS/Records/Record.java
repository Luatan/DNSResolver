package Model.DNS.Records;

import Model.Utils.RecordTypes;

public abstract class Record {
    protected RecordTypes type;
    protected String value;

    public static Record A = new Record(RecordTypes.A) {};
    public static Record AAAA = new Record(RecordTypes.AAAA) {};
    public static Record CNAME = new Record(RecordTypes.CNAME) {};
    public static Record MSG = new Record(RecordTypes.MSG) {};
    public static Record MX = new Record(RecordTypes.MX) {};
    public static Record SOA = new Record(RecordTypes.SOA) {};
    public static Record SRV = new Record(RecordTypes.SRV) {};
    public static Record TXT = new Record(RecordTypes.TXT) {};
    public static Record NS = new Record(RecordTypes.NS) {};

    public static Record setRecord(Record type, String value) {
        type.setValue(value);
        return type;
    }

    public Record(RecordTypes type, String value) {
        setValue(value);
        setType(type);
    }

    public Record(RecordTypes type) {
        setType(type);
    }

    public void setType(RecordTypes type) {
        this.type = type;
    }

    public RecordTypes getType() {
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
        return value;
    }
}
