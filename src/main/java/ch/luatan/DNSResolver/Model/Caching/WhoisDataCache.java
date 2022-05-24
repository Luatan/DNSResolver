package ch.luatan.DNSResolver.Model.Caching;

import ch.luatan.DNSResolver.Model.Utils.Config;
import ch.luatan.DNSResolver.Model.Utils.FileHelper;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class WhoisDataCache extends Cache{
    private final String DOMAIN;
    private File cacheFile;

    public WhoisDataCache(String domain) {
        DOMAIN = domain;
        if (!isCaching()) {
            return;
        }
        setTimeToLive(Config.WHOIS_DATA_CACHE_TTL); //defines how long a file should be kept.
        selectFile(); // search cache folder for existing files
    }

    public void write(List<String> content) {
        if (content.size() < 1) {
            return;
        }
        StringBuilder list = new StringBuilder();
        for (String element:content) {
            list.append(element.trim()).append(",");
        }

        FileHelper.createFile(list.toString(), cacheFile.getPath());
    }

    public List<String> load() {
        return Arrays.asList(Objects.requireNonNull(FileHelper.readFile(cacheFile.getPath())).split(","));
    }

    public boolean isCached() {
        return cacheFile.exists() && isValid(cacheFile);
    }

    private void selectFile() {
        cacheFile = new File(Config.WHOIS_CACHE + DOMAIN + ".tmp");
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
