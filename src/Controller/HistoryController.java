package Controller;

import Model.HistoryFile;
import Model.Utils.JsonAdapter;
import Model.Utils.Config;
import Model.Utils.FileStructure;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.IOException;
import java.io.Reader;


public class HistoryController extends JsonAdapter {
    public HistoryFile history;

    public HistoryController() {
        super(HistoryFile.class);
    }

    @Override
    public void write() {
        write(history, Config.HISTORY_LOG_FILE);
    }

    @Override
    protected void load() {
        try {
            Reader reader = FileStructure.getReader(Config.HISTORY_LOG_FILE);
            history = (HistoryFile) HANDLER.fromJson(reader, type);
            reader.close();
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
