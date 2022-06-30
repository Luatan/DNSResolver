package ch.luatan.DNSResolver.Model.Utils;

public class AppConfig {
    private final boolean cache;
    private final int whois_cache_ttl;
    private final int whois_ext_cache_ttl;
    private boolean showEmptyRecords;
    private boolean darkmode;
    private boolean ignoreDNSSEC;

    public AppConfig(boolean showEmptyRecords, boolean darkmode, boolean cache, int whois_cache_ttl, int whois_ext_cache_ttl, boolean dnssec_validation) {
        this.showEmptyRecords = showEmptyRecords;
        this.darkmode = darkmode;
        this.cache = cache;
        this.whois_cache_ttl = whois_cache_ttl;
        this.whois_ext_cache_ttl = whois_ext_cache_ttl;
        this.ignoreDNSSEC = dnssec_validation;
    }

    public boolean isIgnoreDNSSEC() {
        return ignoreDNSSEC;
    }

    public void setIgnoreDNSSEC(boolean ignoreDNSSEC) {
        this.ignoreDNSSEC = ignoreDNSSEC;
    }

    public boolean isShowEmptyRecords() {
        return showEmptyRecords;
    }

    public void setShowEmptyRecords(boolean showEmptyRecords) {
        this.showEmptyRecords = showEmptyRecords;
    }

    public boolean isDarkmode() {
        return darkmode;
    }

    public void setDarkmode(boolean darkmode) {
        this.darkmode = darkmode;
    }

    public boolean isCache() {
        return cache;
    }

    public int getWhois_cache_ttl() {
        return whois_cache_ttl;
    }

    public int getWhois_ext_cache_ttl() {
        return whois_ext_cache_ttl;
    }

    @Override
    public String toString() {
        return "AppConfig{" +
                "ShowEmptyRecords=" + showEmptyRecords +
                ", darkmode=" + darkmode +
                ", cache=" + cache +
                ", cacheTime=" + whois_cache_ttl +
                '}';
    }
}
