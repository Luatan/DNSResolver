package DNS;

public class Record {
    private String type;
    private String host;
    private String value;

    public Record(String type, String host, String value) {
        this.type = type;
        this.host = host;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getHost() {
        return host;
    }

    public String getValue() {
        return value;
    }
}
