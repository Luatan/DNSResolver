package Tasks;

import Utils.Config;

public class CacheCleanupTask extends Thread{

    @Override
    public synchronized void run() {
        if (!Config.CACHING) {
            return;
        }
        Config.cleanCacheFiles();
    }
}
