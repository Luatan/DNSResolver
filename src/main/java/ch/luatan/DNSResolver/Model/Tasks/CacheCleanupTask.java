package ch.luatan.DNSResolver.Model.Tasks;

import ch.luatan.DNSResolver.Model.Caching.WhoisDataCache;
import ch.luatan.DNSResolver.Model.Caching.WhoisExtensionCache;
import ch.luatan.DNSResolver.Model.Utils.Config;

import java.io.File;
import java.util.Objects;

public class CacheCleanupTask extends Thread {

    @Override
    public synchronized void run() {
        while (true) {
            if (!Config.CACHING) {
                return;
            }
            //Whois Data
            whoisData();

            //Whois Extensions Cache
            whoisExtensions();

            try {
                this.wait(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void whoisExtensions() {
        File cacheDir = new File(Config.WHOIS_EXT_CACHE);
        if (!cacheDir.exists()) {
            return;
        }
        try {
            for (File file: Objects.requireNonNull(cacheDir.listFiles())) {
                WhoisExtensionCache cache = new WhoisExtensionCache();
                cache.isValid(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void whoisData() {

        File cacheDir = new File(Config.WHOIS_CACHE);
        if (!cacheDir.exists()) {
            return;
        }
        try {
            for (File file: Objects.requireNonNull(cacheDir.listFiles())) {
                WhoisDataCache cache = new WhoisDataCache(file.getName().substring(0, file.getName().indexOf(".tmp")));
                cache.isValid(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
