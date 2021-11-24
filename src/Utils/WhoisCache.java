package Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;


public class WhoisCache {
    private final String PATH = Config.CACHE_FILES;
    private String domain;
    private String fileName;
    private long ttl = 60000;
    private File cacheFile;


    public WhoisCache(String domain) {
        this.domain = domain;
        // domain-time.tmp
        selectFile();
    }

    private void selectFile() {
        cacheFile = new File(PATH + domain + ".tmp");

        for (File file: Objects.requireNonNull(cacheFile.getParentFile().listFiles())) {
            if (file.getName().contains(domain)){
                cacheFile = file;
            }
        }
    }

    public void writeCache(String content) {
        FileStructure.createFile(content, cacheFile.getPath());
    }

    public String readCache() {
        return FileStructure.readFile(cacheFile.getPath());
    }

    public boolean isCached() {
        if (cacheFile.exists() && isValid()) {
            return true;
        }
        return false;
    }

    private boolean isValid() {
        long cacheTime = cacheFile.lastModified();
        System.out.println();
        System.out.println(System.currentTimeMillis() - cacheTime);
        if ((System.currentTimeMillis() - cacheTime) > ttl) {
            try {
                if(Files.deleteIfExists(Paths.get(cacheFile.getAbsolutePath()))) {
                    System.out.println("deleted");
                } else {
                    System.out.println("could not be deleted");
                }
                return false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public void setTimeToLive(int seconds) {
        ttl = seconds;
    }
}
