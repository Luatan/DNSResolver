package Controller;

import Model.Utils.Config;
import Model.Utils.FileStructure;
import Model.Utils.HistoryList;
import Model.Utils.JsonAdapter;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class HistoryController extends JsonAdapter {
    public HistoryList<String> history;

    @Override
    public void write() {
        if (history.isEmpty()) {
            return;
        }
        Map<String, HistoryList<String>> domains = new LinkedHashMap<>();
        domains.put("domains", history);
        write(domains, Config.HISTORY_LOG_FILE);
    }

    @Override
    protected void load() {
        try (Reader reader = FileStructure.getReader(Config.HISTORY_LOG_FILE)){
            ArrayList<String> temp = (ArrayList<String>) HANDLER.fromJson(reader, HashMap.class).get("domains");
            history = new HistoryList<>();
            history.addAll(temp);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException jsonE) {
            reset();
        }
        if (history == null) {
            reset();
        }
    }

    @Override
    protected void reset() {
        System.err.println("History Logfile error.... reseting File");
        File file = new File(Config.HISTORY_LOG_FILE);
        if (file.delete()) {
            System.err.println(Config.HISTORY_LOG_FILE + " was deleted!");
        }

        Config.createHistoryConfig();
        load();
    }
}
