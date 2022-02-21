package Model.Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class JsonAdapter {
    public final static Gson HANDLER = new GsonBuilder().setPrettyPrinting().create();

    public JsonAdapter(){
        load();
    }

    public static void write(Object json, String destinationFile) {
        FileStructure.createFile(HANDLER.toJson(json), destinationFile);
    }

    public abstract void write();

    protected abstract void load();

    protected abstract void reset();
}
