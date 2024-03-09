package br.nullexcept.mux.res;

import br.nullexcept.mux.graphics.Drawable;

public interface AttributeList {
    default int getColor(String name, int defaultValue) {
        return defaultValue;
    }

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
