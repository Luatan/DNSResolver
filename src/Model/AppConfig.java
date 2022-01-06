package Model;

public class AppConfig {
    private boolean showEmptyRecords;
    private boolean darkmode;
    private final boolean cache;
    private final int cacheTime;

    public AppConfig(boolean showEmptyRecords, boolean darkmode, boolean cache, int cacheTime) {
        this.showEmptyRecords = showEmptyRecords;
        this.darkmode = darkmode;
        this.cache = cache;
        this.cacheTime = cacheTime;
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

    public int getCacheTime() {
        return cacheTime;
    }

    @Override
    public String toString() {
        return "AppConfig{" +
                "ShowEmptyRecords=" + showEmptyRecords +
                ", darkmode=" + darkmode +
                ", cache=" + cache +
                ", cacheTime=" + cacheTime +
                '}';
    }
}
