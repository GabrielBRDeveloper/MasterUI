package br.nullexcept.mux.graphics.fonts;

import br.nullexcept.mux.C;
import br.nullexcept.mux.utils.BufferUtils;

import java.io.InputStream;

public abstract class TypefaceFactory {

    protected abstract Typeface decodeTypeface(InputStream input);

    public static Typeface create(InputStream stream){
        return C.TYPEFACE_FACTORY.decodeTypeface(stream);
    }
}
