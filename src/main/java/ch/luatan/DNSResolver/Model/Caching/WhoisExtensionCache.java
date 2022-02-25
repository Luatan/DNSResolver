package ch.luatan.DNSResolver.Model.Caching;

import ch.luatan.DNSResolver.Model.Utils.Config;
import ch.luatan.DNSResolver.Model.Utils.FileStructure;
import ch.luatan.DNSResolver.Model.Utils.JsonAdapter;
import ch.luatan.DNSResolver.Model.Whois.WhoisServer;

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
