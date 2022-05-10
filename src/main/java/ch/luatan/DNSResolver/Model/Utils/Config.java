package ch.luatan.DNSResolver.Model.Utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Config {
    public static boolean CACHING = true;
    public static int WHOIS_DATA_CACHE_TTL = 120;
    public static int WHOIS_EXT_CACHE_TTL = 5184000;
    public static final String WHOIS_CACHE = "cache/whois/";
    public static final String WHOIS_EXT_CACHE = "cache/ext/";
    public static final String SETTINGS_CONF_FILE = "config/settings.json";
    public static final String HISTORY_LOG_FILE = "logs/history.json";
    public static final String IP_API_LOG_FILE = "logs/IP_API_req.json";

    public static void createHistoryConfig(){
        if (!FileHelper.fileExists(HISTORY_LOG_FILE)) {
            Map<String, List<String>> domains = new LinkedHashMap<>();
            domains.put("domains", new ArrayList<>());
            JsonAdapter.write(domains, HISTORY_LOG_FILE);
        }
    }

    public static void createSettingsConfig() {
        if (!FileHelper.fileExists(SETTINGS_CONF_FILE)){
            Map<String,Object> settings = new LinkedHashMap<>();
            settings.put("ShowEmptyRecords", Boolean.FALSE);
            settings.put("darkmode", Boolean.FALSE);
            settings.put("cache", CACHING);
            settings.put("whois_cache_ttl", WHOIS_DATA_CACHE_TTL);
            settings.put("whois_ext_cache_ttl", WHOIS_EXT_CACHE_TTL);
            JsonAdapter.write(settings, SETTINGS_CONF_FILE);
        }
    }
}
