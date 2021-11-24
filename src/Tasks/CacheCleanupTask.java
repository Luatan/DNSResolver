package Tasks;

import Utils.Config;

public class CacheCleanupTask extends Thread{

    @Override
    public synchronized void run() {
        System.out.println("running cache cleanup!");
        Config.cleanCacheFiles();
    }
}
