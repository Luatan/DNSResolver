package Records;

public class MX extends Record{
    private int priority;

    public MX(String type,String value, int priority) {
        super(type, value);
        setPriority(priority);
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return priority + "," + value;
    }
}
