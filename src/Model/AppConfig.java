package Model;

public class AppConfig {
    private boolean showEmptyRecords;
    private boolean darkmode;
    private final boolean cache;
    private final int whois_cache_seconds;
    private final int whois_ext_cache_days;

    public AppConfig(boolean showEmptyRecords, boolean darkmode, boolean cache, int cacheTime, int whois_ext_cache_days) {
        this.showEmptyRecords = showEmptyRecords;
        this.darkmode = darkmode;
        this.cache = cache;
        this.whois_cache_seconds = cacheTime;
        this.whois_ext_cache_days = whois_ext_cache_days;
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

    public int getWhois_cache_seconds() {
        return whois_cache_seconds;
    }

    public int getWhois_ext_cache_days() {
        return whois_ext_cache_days;
    }

    @Override
    public String toString() {
        return "AppConfig{" +
                "ShowEmptyRecords=" + showEmptyRecords +
                ", darkmode=" + darkmode +
                ", cache=" + cache +
                ", cacheTime=" + whois_cache_seconds +
                '}';
    }
}
