package br.nullexcept.mux.res;

import br.nullexcept.mux.graphics.Drawable;
import br.nullexcept.mux.lang.Function;

public interface AttributeList {
    default int getColor(String name, int defaultValue) {
        return defaultValue;
    }

    void searchText(String name, Function<CharSequence> apply);
    void searchColor(String name, Function<Integer> apply);
    void searchDimension(String name, Function<Float> apply);
    void searchDrawable(String name, Function<Drawable> apply);
    void searchFloat(String name, Function<Float> apply);
    void searchRaw(String name, Function<String> apply);

    String getRawValue(String name);

    default CharSequence getText(String name) {
        return "";
    }

    default float getDimension(String name, float defaultValue) {
        return defaultValue;
    }

    default Drawable getDrawable(String name) {
        return null;
    }
}
