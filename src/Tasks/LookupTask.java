package Tasks;

import javafx.concurrent.Task;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class LookupTask extends Task<String> {
    private final String host;

    public LookupTask(String host) {
        this.host = host;
    }

    @Override
    protected String call() {
        long time = System.currentTimeMillis();
        InetAddress inetHost;
        try {
            inetHost = InetAddress.getByName(host);

            updateValue(inetHost.getHostName());
            updateMessage(inetHost.getHostAddress());
        } catch (UnknownHostException e) {
            updateValue(host);
        }
        System.out.println("Lookup of " + host + " took: " + (System.currentTimeMillis() - time) + " ms");
        return String.valueOf(valueProperty());
    }
}
