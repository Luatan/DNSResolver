package ch.luatan.DNSResolver.Model.DNS;

public enum SpecialType implements Type {
    HYPERLINK, HYPERLINKSYMBOL,SPF, RECORD, ANY, MSG;

    @Override
    public Record getRecord() {
        return new Record(this);
    }
}
