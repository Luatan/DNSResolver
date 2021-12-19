package Model;

import Utils.FileStructure;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class JsonAdapter {
    protected final Class type;
    public final static Gson HANDLER = new GsonBuilder().setPrettyPrinting().create();

    public JsonAdapter(Class type) {
        this.type = type;
        load();
    }

    public static void write(Object json, String destinationFile) {
        FileStructure.createFile(HANDLER.toJson(json), destinationFile);
    }

    public abstract void write();

    protected abstract void load();

    protected abstract void reset();
}
