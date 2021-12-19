package ch.luatan.Tasks;

import ch.luatan.Utils.Config;

public class CacheCleanupTask extends Thread{

    @Override
    public synchronized void run() {
        if (!Config.CACHING) {
            return;
        }
        System.out.println("running cache cleanup!");
        Config.cleanCacheFiles();
    }
}
