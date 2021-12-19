package ch.simon.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class WhoisCache {
    private final String DOMAIN;
    private File cacheFile;
    private long ttl;


    public WhoisCache(String domain) {
        DOMAIN = domain;
        if (!Config.CACHING) {
            return;
        }
        setTimeToLive(Config.CACHE_TIME_TO_LIVE); //defines how long a file should be kept.
        selectFile(); // search cache folder for existing files
    }

    public void setTimeToLive(int seconds) {
        ttl = (long) seconds * 1000;
    }

    public void writeCache(List<String> content) {
        String list = "";
        for (String element:content) {
            list += element.trim() + ",";
        }

        FileStructure.createFile(list, cacheFile.getPath());
    }

    public String readCache() {
        return FileStructure.readFile(cacheFile.getPath());
    }

    public List<String> readCacheByLine() {
        return Arrays.asList(readCache().split(","));
    }

    public boolean isCached() {
        return cacheFile.exists() && isValid();
    }

    public boolean isValid() {
        long cacheTime = cacheFile.lastModified();
        if ((System.currentTimeMillis() - cacheTime) > ttl) {
            try {
                if (!Files.deleteIfExists(Paths.get(cacheFile.getAbsolutePath()))) {
                    System.err.println("Cache File" + cacheFile.getName() + "could not be deleted");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    private void selectFile() {
        cacheFile = new File(Config.CACHE_FILES + DOMAIN + ".tmp");
        File parent = cacheFile.getParentFile();
        if (parent.exists()) {
            // search cache directory for an already existing file
            for (File file : Objects.requireNonNull(parent.listFiles())) {
                if (file.getName().equals(DOMAIN + ".tmp")) {
                    cacheFile = file;
                }
            }
        }
    }
}
