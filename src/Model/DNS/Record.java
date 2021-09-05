package Model.DNS;

public class Record {
    private String type;
    private String host;
    private String value;
    private int ttl;

    public Record(String type, String host, String value, int ttl) {
        this.type = type;
        this.host = host;
        this.value = value;
        this.ttl = ttl;
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

    public int getTtl() {
        return ttl;
    }
}
