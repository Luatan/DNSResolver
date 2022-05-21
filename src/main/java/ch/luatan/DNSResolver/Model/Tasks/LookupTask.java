package ch.luatan.DNSResolver.Model.Tasks;

import ch.luatan.DNSResolver.DNSResolver;
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

            updateMessage(inetHost.getHostAddress());
            return inetHost.getHostName();
        } catch (UnknownHostException e) {
            updateValue(host);
        }
        DNSResolver.LOGGER.debug("Nameresolution of " + host + " took: " + (System.currentTimeMillis() - time) + " ms");
        return host;
    }
}
