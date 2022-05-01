package ch.luatan.DNSResolver.Model.DNS;

public enum SpecialType implements Type {
    HYPERLINK, HYPERLINKSYMBOL,SPF, RECORD, ANY, MSG, NS;

    @Override
    public Record getRecord() {
        return new Record(this);
    }
}
