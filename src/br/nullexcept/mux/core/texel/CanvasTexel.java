package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.graphics.*;
import br.nullexcept.mux.hardware.GLES;
import org.lwjgl.opengles.GLES20;

class CanvasTexel implements Canvas {
    private static long CURRENT_CANVAS = -1;
    private final GLFramebuffer framebuffer;
    private final long id = (long) (Math.random() * Long.MAX_VALUE);
    private final Point translation = new Point();

    public CanvasTexel(int width, int height) {
        this.framebuffer = new GLFramebuffer(width, height);
    }

    public void begin() {
        if (CURRENT_CANVAS != id) {
            VgTexel.endFrame();
            framebuffer.bind();
            VgTexel.beginFrame(framebuffer.getWidth(), framebuffer.getHeight());
        }
    }

    @Override
    public void reset() {
        GLES.glClearColor(0, 0, 0, 0);
        GLES.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT | GLES.GL_STENCIL_BUFFER_BIT);
        translation.set(0, 0);
    }

    @Override
    public void end() {
        VgTexel.endFrame();
        framebuffer.unbind();
    }

    @Override
    public void drawColor(int color) {
        VgTexel.setColor(color);
        drawRect(0, 0, getWidth(), getHeight(), VgTexel.getPaint());
    }

    @Override
    public void drawRect(int left, int top, int right, int bottom, Paint paint) {
        VgTexel.beginElement();
        VgTexel.applyPaint(paint);
        VgTexel.drawRect(left, top, right - left, bottom - top);
        VgTexel.endElement();
    }

    @Override
    public void drawRect(Rect rect, Paint paint) {
        drawRect(rect.left, rect.top, rect.right, rect.bottom, paint);
    }

    @Override
    public void drawText(String text, int x, int y, Paint paint) {
        if (text == null) {
            text = "[NULL]";
        }
        VgTexel.beginElement();
        VgTexel.applyPaint(paint);
        VgTexel.drawText(x, y, text);
        VgTexel.endElement();
    }

    @Override
    public void translate(int x, int y) {
        translation.x += x;
        translation.y += y;
    }

    @Override
    public void drawBitmap(Rect rect, Bitmap bitmap, Paint paint) {
        drawBitmap(rect.left, rect.top, rect.width(), rect.height(), bitmap, paint);
    }

    @Override
    public void drawBitmap(int x, int y, Bitmap bitmap, Paint paint) {
        drawBitmap(x, y, bitmap.getWidth(), bitmap.getHeight(), bitmap, paint);
    }

    @Override
    public void drawBitmap(int x, int y, int width, int height, Bitmap bitmap, Paint paint) {
        if (!(bitmap instanceof TexelBitmap)){
            throw new IllegalArgumentException("Invalid bitmap, bitmap core and canvas core is different!");
        }
        TexelBitmap texelBitmap = (TexelBitmap)bitmap;
        VgTexel.beginElement();
        VgTexel.drawImage(texelBitmap,x,y,width,height, 0,0, texelBitmap.getWidth(), texelBitmap.getHeight());
        VgTexel.endElement();
    }

    @Override
    public void dispose() {
        framebuffer.dispose();
    }

    @Override
    public int getWidth() {
        return framebuffer.getWidth();
    }

    @Override
    public int getHeight() {
        return framebuffer.getHeight();
    }

    public GLFramebuffer getFramebuffer() {
        return framebuffer;
    }
}