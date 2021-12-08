package Model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class JsonAdapter {
    protected final Class type;
    protected final Gson HANDLER = new GsonBuilder().setPrettyPrinting().create();

    public JsonAdapter(Class type) {
        this.type = type;
        load();
    }

    public abstract void write();

    protected abstract void load();

    protected abstract void reset();
}
