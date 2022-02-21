package Model.Utils;

import Model.DNS.Record;

public enum DNSType implements Type {
    A, AAAA, CNAME, MX, SRV, TXT, SOA;

    @Override
    public Record getRecord() {
        return new Record(this);
    }
}
