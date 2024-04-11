package br.nullexcept.mux.graphics.fonts;

import br.nullexcept.mux.utils.BufferUtils;

import java.io.InputStream;

public class TypefaceFactory {

    public static Typeface create(InputStream stream){
        return new Typeface(BufferUtils.allocateStream(stream));
    }
}
