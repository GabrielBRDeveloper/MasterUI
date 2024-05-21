package br.nullexcept.mux.app;

import br.nullexcept.mux.lang.Parcel;
import br.nullexcept.mux.lang.Valuable;

import java.util.ArrayList;
import java.util.HashMap;

public class Launch<T> extends Parcel {
    private final Class<T> clazz;
    private final Valuable<T> creator;
    private static HashMap<String, Valuable> cacheCreators = new HashMap<>();
    private final ArrayList<Integer> flags = new ArrayList<>();

    public Launch(Class<T> clazz, Valuable<T> creator) {
        this.clazz = clazz;
        this.creator = creator;
    }

    public boolean hasFlag(int flag) {
        return flags.contains(flag);
    }

    public Launch<T> addFlags(int... flags) {
        for (int flag: flags) {
            if (!hasFlag(flag)) {
                this.flags.add(flag);
            }
        }
        return this;
    }

    @Override
    public Launch<T> put(String name, Number value) {
        super.put(name, value);
        return this;
    }

    @Override
    public Launch<T> put(String name, String value) {
        super.put(name, value);
        return this;
    }

    @Override
    public Launch<T> put(String name, Boolean value) {
        super.put(name, value);
        return this;
    }

    public Launch<T> subFlags(int... flags) {
        for (int flag: flags) {
            if (hasFlag(flag)) {
                this.flags.remove((Integer) flag);
            }
        }
        return this;
    }

    @SuppressWarnings("Dont use that with native-image!!")
    public Launch(Class<T> clazz) {
        this(clazz, () -> {
            try {
                if (cacheCreators.containsKey(clazz.getName())) {
                    return (T) cacheCreators.get(clazz.getName()).get();
                }
                return clazz.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Cannot create class from constructor, your are using native-image or constructor has arguments.", e);
            }
        });
    }

    public Class<T> getLaunchClass() {
        return clazz;
    }

    T make() {
        return (T) creator.get();
    }

    @SuppressWarnings("Case you want use native-image you can register class constructor after use")
    public static <T> void registerCreator(Class<T> clazz, Valuable<T> creator) {
        cacheCreators.put(clazz.getName(), creator);
    }
}
