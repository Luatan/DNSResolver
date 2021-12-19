package ch.simon.Model;

import ch.simon.Utils.FileStructure;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.output.FileWriterWithEncoding;

import java.io.IOException;

public abstract class JsonAdapter {
    protected final Class type;
    public final static Gson HANDLER = new GsonBuilder().setPrettyPrinting().create();

    public JsonAdapter(Class type) {
        this.type = type;
        load();
    }

    public static void write(Object json, String destinationFile) {
        try {
            FileWriterWithEncoding file = new FileWriterWithEncoding(FileStructure.DIR_HOME + destinationFile, "utf-8");
            file.write(HANDLER.toJson(json));
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract void write();

    protected abstract void load();

    protected abstract void reset();
}
