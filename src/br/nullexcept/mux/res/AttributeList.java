package br.nullexcept.mux.res;

import br.nullexcept.mux.graphics.ColorStateList;
import br.nullexcept.mux.graphics.Drawable;
import br.nullexcept.mux.graphics.fonts.Typeface;
import br.nullexcept.mux.lang.Function;

public interface AttributeList {
    void searchText(String name, Function<CharSequence> apply);

    void searchColor(String name, Function<Integer> apply);

    void searchColorList(String name, Function<ColorStateList> apply);

    void searchDimension(String name, Function<Float> apply);

    void searchDrawable(String name, Function<Drawable> apply);

    void searchFloat(String name, Function<Float> apply);

    void searchRaw(String name, Function<String> apply);

    void searchBoolean(String name, Function<Boolean> apply);

    void searchFont(String name, Function<Typeface> apply);

    String[] names();

    String getRawValue(String name);

    default Typeface getFont(String name) {
        return null;
    }

    default CharSequence getText(String name) {
        return "";
    }

    default int getColor(String name, int defaultValue) {
        return defaultValue;
    }

    default ColorStateList getColorList(String name) {
        return null;
    }

    default float getDimension(String name, float defaultValue) {
        return defaultValue;
    }

    default Drawable getDrawable(String name) {
        return null;
    }

    boolean contains(String name);
}