package ch.luatan.DNSResolver.Model.DNS;

public enum AdditionalTypes implements Type{
    NS, CAA, DS, DNSKEY,TSIG, TLSA ;

    @Override
    public Record getRecord() {
        return new Record(this);
    }
}
