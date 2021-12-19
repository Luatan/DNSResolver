package ch.simon.Model.DNS.Records;

public class MX extends Record{
    private int priority;

    public MX(String value, int priority) {
        super("MX", value);
        setPriority(priority);
    }

    public MX(String value) {
        super("MX", value);
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return priority + "," + value;
    }
}
