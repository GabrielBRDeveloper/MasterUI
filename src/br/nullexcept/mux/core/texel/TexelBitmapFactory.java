package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.graphics.Bitmap;
import br.nullexcept.mux.graphics.BitmapFactory;
import br.nullexcept.mux.utils.BufferUtils;

import java.io.InputStream;
import java.nio.ByteBuffer;

class TexelBitmapFactory extends BitmapFactory {
    @Override
    protected Bitmap _createBitmap(int width, int height, Bitmap.Format format) {
        return null;
    }

    @Override
    protected Bitmap _openStreamBitmap(InputStream stream) {
        ByteBuffer bytes = BufferUtils.allocateStream(stream);
        Bitmap bitmap = new TexelBitmap(bytes);
        bytes.limit(0);
        return bitmap;
    }
}
