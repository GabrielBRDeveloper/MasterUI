package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.C;
import br.nullexcept.mux.graphics.Bitmap;
import br.nullexcept.mux.graphics.BitmapFactory;
import br.nullexcept.mux.utils.BufferUtils;
import org.lwjgl.nanovg.NanoVG;

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

    @Override
    protected Bitmap _openRawBitmap(int width, int height, ByteBuffer stream) {
        return new TexelBitmap(NanoVG.nvgCreateImageRGBA(C.VG_CONTEXT,width,height,0,stream));
    }
}
