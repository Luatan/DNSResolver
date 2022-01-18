package Caching;

import Model.Utils.Config;
import Model.Utils.FileStructure;
import Model.Utils.JsonAdapter;
import Model.Utils.WhoisServer;

import java.io.File;
import java.io.IOException;

public class WhoisExtensionCache extends Cache {

    public WhoisExtensionCache() {
        setTimeToLive(Config.WHOIS_EXT_CACHE_TTL);
    }

    public void write(WhoisServer server) {
        FileStructure.createFile(JsonAdapter.HANDLER.toJson(server), getFileObject(server.getDomain()).getPath());
    }

    public boolean isCached(String ext) {
        File cache = getFileObject(ext);
        return cache.exists() && isValid(cache);
    }

    private File getFileObject(String ext) {
        return new File(Config.WHOIS_EXT_CACHE + ext + ".json");
    }

    public WhoisServer load(String ext) throws IOException {
        if (isCached(ext)) {
            return JsonAdapter.HANDLER.fromJson(FileStructure.readFile(getFileObject(ext).getPath()), WhoisServer.class);
        }
        throw new IOException();
    }
}
