import javafx.concurrent.Task;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

public class LookupTask extends Task<String> {
    private String host;

    LookupTask(String host) {
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
            updateValue("Unknown Host!");
        }
        System.out.println("Lookup took: " + (System.currentTimeMillis() - time));
        return String.valueOf(valueProperty());
    }
}
