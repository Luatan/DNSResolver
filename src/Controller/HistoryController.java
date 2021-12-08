package Controller;

import Model.HistoryFile;
import Model.JsonAdapter;
import Utils.Config;
import Utils.FileStructure;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.output.FileWriterWithEncoding;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;


public class HistoryController extends JsonAdapter {
    public HistoryFile history;

    public HistoryController() {
        super(HistoryFile.class);
    }

    @Override
    public void write() {
        try {
            FileWriterWithEncoding file = new FileWriterWithEncoding(FileStructure.DIR_HOME + Config.HISTORY_LOG_FILE, "utf-8");
            file.write(HANDLER.toJson(history));
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void load() {
        try {
            Reader reader = Files.newBufferedReader(Paths.get(Config.HISTORY_LOG_FILE));
            history = (HistoryFile) HANDLER.fromJson(reader,type);
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
        System.err.println("Settings File error.... reseting File");
        File file = new File(Config.HISTORY_LOG_FILE);
        if (file.delete()) {
            System.out.println("true");
        } else {
            System.out.println("false");
        }

        Config.createHistoryConfig();
        load();
    }
}
