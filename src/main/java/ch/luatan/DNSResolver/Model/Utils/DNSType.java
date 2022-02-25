package ch.luatan.DNSResolver.Model.Utils;

import ch.luatan.DNSResolver.Model.DNS.Record;

public enum DNSType implements Type {
    A, AAAA, CNAME, MX, SRV, TXT, SOA;

    @Override
    public Record getRecord() {
        return new Record(this);
    }
}
