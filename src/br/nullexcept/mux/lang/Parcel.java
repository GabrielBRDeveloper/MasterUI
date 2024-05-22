package br.nullexcept.mux.lang;

import java.io.Serializable;
import java.util.HashMap;

public class Parcel {
    private final HashMap<String, Serializable> data = new HashMap<>();

    private Number getNumber(String key, Number def) {
        if (data.containsKey(key)) {
            return def;
        } else {
            return (Number) data.get(key);
        }
    }

    public boolean has(String name) {
        return data.containsKey(name);
    }

    public Parcel put(String name, Number value) {
        data.put(name, value);
        return this;
    }

    public Parcel put(String name, Boolean value) {
        data.put(name, value);
        return this;
    }

    public Parcel put(String name, String value) {
        data.put(name, value);
        return this;
    }

    public boolean getBool(String name) {
        return (Boolean) data.get(name);
    }

    public String getString(String name) {
        return (String) data.get(name);
    }

    public int getInt(String name, int def) {
        return getNumber(name, def).intValue();
    }

    public float getFloat(String name, float def) {
        return getNumber(name, def).floatValue();
    }

    public double getDouble(String name, double def) {
        return getNumber(name, def).doubleValue();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Parcel { \n");
        for (String key: data.keySet()) {
            builder.append(key).append(": ").append(data.get(key)).append('\n');
        }
        builder.append("}");
        return builder.toString();
    }
}
