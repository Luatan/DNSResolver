package Records;

public abstract class Record {
    protected String value;
    protected String type;

    public Record(String type,String value) {
        setValue(value);
        setType(type);
    }

    public Record(String type) {
        setType(type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
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
