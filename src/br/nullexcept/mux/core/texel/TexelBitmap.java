package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.C;
import br.nullexcept.mux.app.Looper;
import br.nullexcept.mux.graphics.Bitmap;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGLES2;

import java.nio.ByteBuffer;

class TexelBitmap implements Bitmap {
    private final int width;
    private final int height;
    private final int id;
    private boolean disposed = false;
    int texture;

    public TexelBitmap() {
        this.id = 0;
        this.disposed = true;
        width = 0;
        height = 0;
    }

    public TexelBitmap(int id) {
        this.id = id;
        int[][] sizes = new int[2][1];
        NanoVG.nnvgImageSize(C.VG_CONTEXT, id, sizes[0], sizes[1]);
        width = sizes[0][0];
        height = sizes[1][0];
        setupImage(NanoVGGLES2.nvglImageHandle(C.VG_CONTEXT, id));
    }

    public TexelBitmap(ByteBuffer encodedData){
        this(NanoVG.nvgCreateImageMem(C.VG_CONTEXT,0,encodedData));
    }

    private void setupImage(int texture){
        this.texture = texture;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public Format getFormat() {
        return Format.ARGB;
    }

    @Override
    public boolean isValid() {
        return !disposed;
    }

    @Override
    public void dispose() {
        disposed = true;
        NanoVG.nvgDeleteImage(C.VG_CONTEXT,id);
    }

    @Override
    protected void finalize() throws Throwable {
        Looper.getMainLooper().post(()->{
            try {
                NanoVG.nvgDeleteImage(C.VG_CONTEXT, id);
            } catch (Exception e){}
        });
        super.finalize();
    }

    public int id() {
        return id;
    }
}
