package br.nullexcept.mux.graphics;

import br.nullexcept.mux.C;

import java.io.InputStream;

public abstract class BitmapFactory {

    protected abstract Bitmap _createBitmap(int width, int height, Bitmap.Format format);
    protected abstract Bitmap _openStreamBitmap(InputStream stream);

    public static Bitmap createBitmap(int width, int height, Bitmap.Format format){
        return C.BITMAP_FACTORY._createBitmap(width, height, format);
    }

    public static Bitmap createBitmap(int width, int height){
        return createBitmap(width, height, Bitmap.Format.ARGB);
    }

    public static Bitmap openBitmap(InputStream input){
        return C.BITMAP_FACTORY._openStreamBitmap(input);
    }
}