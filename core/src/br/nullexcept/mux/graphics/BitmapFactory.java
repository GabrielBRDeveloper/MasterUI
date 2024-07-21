package br.nullexcept.mux.graphics;

import br.nullexcept.mux.C;

import java.io.InputStream;
import java.nio.ByteBuffer;

public abstract class BitmapFactory {

    protected abstract Bitmap _createBitmap(int width, int height, Bitmap.Format format);
    protected abstract Bitmap _openStreamBitmap(InputStream stream);
    protected abstract Bitmap _openRawBitmap(int width, int height, ByteBuffer stream);


    public static Bitmap createBitmapFromRGBA(int width, int height, ByteBuffer buffer) {
        return C.BITMAP_FACTORY._openRawBitmap(width, height, buffer);
    }

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