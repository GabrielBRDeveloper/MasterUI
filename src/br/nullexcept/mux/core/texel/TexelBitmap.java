package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.C;
import br.nullexcept.mux.graphics.Bitmap;
import br.nullexcept.mux.hardware.GLES;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGLES2;

import java.nio.ByteBuffer;

class TexelBitmap implements Bitmap {
    private static final int TEXTURE_2D = GLES.GL_TEXTURE27;
    private final int width;
    private final int height;
    private final int id;
    private boolean disposed = false;

    public TexelBitmap(ByteBuffer encodedData){
        id = NanoVG.nvgCreateImageMem(C.VG_CONTEXT,0,encodedData);
        int[][] sizes = new int[2][1];
        NanoVG.nnvgImageSize(C.VG_CONTEXT, id, sizes[0], sizes[1]);
        width = sizes[0][0];
        height = sizes[1][0];
        setupImage(NanoVGGLES2.nvglImageHandle(C.VG_CONTEXT, id));
    }

    private void setupImage(int texture){
        System.err.println("Implement GLES setup for: "+texture);
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
    public int getPixel(int x, int y) {
        return 0;
    }

    @Override
    public void setPixel(int x, int y) {

    }

    @Override
    public Format getFormat() {
        return null;
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

    public int id() {
        return id;
    }
}