package ch.luatan.Model.DNS.Records;

public class SOA extends Record {

    public SOA(String value) {
        super("SOA");
        this.setValue(value);
    }
}
