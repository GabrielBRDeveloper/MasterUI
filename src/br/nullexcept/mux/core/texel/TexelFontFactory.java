package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.graphics.fonts.Typeface;
import br.nullexcept.mux.graphics.fonts.TypefaceFactory;
import br.nullexcept.mux.res.AssetsManager;
import br.nullexcept.mux.utils.BufferUtils;

import java.io.InputStream;

public class TexelFontFactory extends TypefaceFactory {

    public TexelFontFactory() {
        Typeface.DEFAULT = decodeTypeface(TexelFontFactory.class.getResourceAsStream("/res/fonts/Roboto/Roboto-Regular.ttf"));
    }

    @Override
    protected Typeface decodeTypeface(InputStream input) {
        return new TexelFont(BufferUtils.allocateStream(input));
    }
}
