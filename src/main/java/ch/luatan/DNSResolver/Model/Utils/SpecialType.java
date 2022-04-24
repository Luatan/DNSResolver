package ch.luatan.DNSResolver.Model.Utils;

import ch.luatan.DNSResolver.Model.DNS.Record;

public enum SpecialType implements Type {
    HYPERLINK, HYPERLINKSYMBOL,SPF, RECORD, ANY, MSG, NS;

    @Override
    public Record getRecord() {
        return new Record(this);
    }
}
