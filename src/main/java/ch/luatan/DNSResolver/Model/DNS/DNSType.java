package ch.luatan.DNSResolver.Model.DNS;

public enum DNSType implements Type {
    A, AAAA, CNAME, MX, SRV, TXT, SOA, HINFO;

    @Override
    public Record getRecord() {
        return new Record(this);
    }
}
