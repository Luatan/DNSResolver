package Model.Utils;

import Model.DNS.Record;

public enum SpecialType implements Type {
    HYPERLINK, SPF, RECORD, ANY, MSG, NS;

    @Override
    public Record getRecord() {
        return new Record(this);
    }
}
