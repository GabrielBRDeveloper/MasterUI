package br.nullexcept.mux.graphics.fonts;

import br.nullexcept.mux.res.AssetsManager;
import br.nullexcept.mux.utils.BufferUtils;

public class TypefaceFactory {
    public static void createDefaults() {
        Typeface.DEFAULT = new Typeface(BufferUtils.allocateStream(AssetsManager.openDocument("fonts/Roboto/Roboto-Regular.ttf")));
    }
}
