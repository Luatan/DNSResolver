package Model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class JSON {

    public JSON() {
        load();
    }

    protected final Gson HANDLER = new GsonBuilder().setPrettyPrinting().create();

    public abstract void write();

    protected abstract void load();

    protected abstract void reset();
}
