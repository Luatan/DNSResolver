package Model;

public class AppConfig {
    private boolean ShowEmptyRecords;
    private boolean darkmode;
    private final boolean cache;
    private final int cacheTime;

    public AppConfig(boolean showEmptyRecords, boolean darkmode, boolean cache, int cacheTime) {
        ShowEmptyRecords = showEmptyRecords;
        this.darkmode = darkmode;
        this.cache = cache;
        this.cacheTime = cacheTime;
    }

    public boolean isShowEmptyRecords() {
        return ShowEmptyRecords;
    }

    public void setShowEmptyRecords(boolean showEmptyRecords) {
        ShowEmptyRecords = showEmptyRecords;
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
                "ShowEmptyRecords=" + ShowEmptyRecords +
                ", darkmode=" + darkmode +
                ", cache=" + cache +
                ", cacheTime=" + cacheTime +
                '}';
    }
}
