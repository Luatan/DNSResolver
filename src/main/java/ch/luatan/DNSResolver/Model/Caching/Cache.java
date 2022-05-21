package ch.luatan.DNSResolver.Model.Caching;

import ch.luatan.DNSResolver.DNSResolver;
import ch.luatan.DNSResolver.Model.Utils.Config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class Cache {
    protected long ttl;

    protected boolean isCaching() {
        return Config.CACHING;
    }

    protected void setTimeToLive(int seconds) {
        ttl = (long) seconds * 1000;
    }

    public boolean isValid(File cacheFile) {
        long cacheTime = cacheFile.lastModified();
        if ((System.currentTimeMillis() - cacheTime) > ttl) {
            try {
                if (!Files.deleteIfExists(Paths.get(cacheFile.getAbsolutePath()))) {
                    DNSResolver.LOGGER.error("Cache File" + cacheFile.getName() + "could not be deleted");
                } else {
                    DNSResolver.LOGGER.debug("removed: " + cacheFile.getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }
}
