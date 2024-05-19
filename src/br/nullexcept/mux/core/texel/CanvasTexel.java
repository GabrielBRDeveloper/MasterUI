package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.graphics.*;
import br.nullexcept.mux.graphics.shape.ShapeList;
import br.nullexcept.mux.hardware.GLES;
import br.nullexcept.mux.utils.Log;
import org.lwjgl.opengles.GLES20;

class CanvasTexel implements Canvas {
    private static long CURRENT_CANVAS = -1;
    private final GLFramebuffer framebuffer;
    private float alpha = 1.0f;
    private final long id = (long) (Math.random() * Long.MAX_VALUE);

    public CanvasTexel(int width, int height) {
        this.framebuffer = new GLFramebuffer(width, height);
    }

    public void begin() {
        VgTexel.endFrame();
        VgTexel.setAlpha(alpha);
        framebuffer.bind();
        VgTexel.beginFrame(framebuffer.getWidth(), framebuffer.getHeight());
        CURRENT_CANVAS = id;
    }

    private void check() {
        if (CURRENT_CANVAS != id) {
            Log.error("CanvasTexel", "Switching canvas!");
            begin();
        }
    }

    @Override
    public void reset() {
        check();
        this.alpha = 1.0f;
        GLES.glClearColor(0, 0, 0, 0);
        GLES.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT | GLES.GL_STENCIL_BUFFER_BIT);
    }

    @Override
    public void end() {
        VgTexel.endFrame();
        framebuffer.unbind();
    }


    public void alpha(float alpha){
        this.alpha *= alpha;
        VgTexel.setAlpha(this.alpha);
    }

    @Override
    public void clip(ShapeList list) {
        check();
        VgTexel.beginClip();
        VgTexel.getPaint().setColor(Color.WHITE);
        list.asArray().forEach(shape -> {
            shape.draw(this, VgTexel.getPaint());
        });
        VgTexel.endClip(framebuffer.getFramebuffer());
    }

    @Override
    public void drawColor(int color) {
        check();
        VgTexel.setColor(color);
        drawRect(0, 0, getWidth(), getHeight(), VgTexel.getPaint());
    }

    @Override
    public void drawRect(int left, int top, int right, int bottom, Paint paint) {
        check();
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
    public void drawText(CharSequence text, int x, int y, Paint paint) {
        check();
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
        check();
        VgTexel.move(x,y);
    }

    @Override
    public void rotate(float angle) {
        check();
        VgTexel.rotate(angle);
    }

    @Override
    public void drawEllipse(int left, int top, int right, int bottom, Paint paint) {
        check();
        VgTexel.beginElement();
        VgTexel.applyPaint(paint);
        VgTexel.drawEllipse(left, top, right-left, bottom-top);
        VgTexel.endElement();
    }

    @Override
    public void drawRoundRect(int left, int top, int right, int bottom, int radius, Paint paint) {
        drawRoundRect(left, top, right, bottom, radius,radius,radius,radius, paint);
    }

    @Override
    public void drawRoundRect(int left, int top, int right, int bottom, int radiusLeft, int radiusTop, int radiusRight, int radiusBottom, Paint paint) {
        check();
        VgTexel.beginElement();
        VgTexel.applyPaint(paint);
        VgTexel.drawRoundedRect(left,top,right-left, bottom-top, radiusLeft, radiusTop, radiusRight, radiusBottom);
        VgTexel.endElement();
    }

    @Override
    public void drawPath(Path path, int x, int y, Paint paint) {
        check();
        VgTexel.beginElement();
        VgTexel.applyPaint(paint);
        translate(x,y);
        VgTexel.drawPath(path);
        translate(-x, -y);
        VgTexel.endElement();
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
        drawBitmap(x,y,width,height, 0,0, bitmap.getWidth(), bitmap.getHeight(), bitmap,paint);
    }

    @Override
    public void drawBitmap(int x, int y, int width, int height, int srcX, int srcY, int srcWidth, int srcHeight, Bitmap bitmap, Paint paint) {
        check();
        if (!(bitmap instanceof TexelBitmap)){
            throw new IllegalArgumentException("Invalid bitmap, bitmap core and canvas core is different!");
        }
        TexelBitmap texelBitmap = (TexelBitmap)bitmap;
        VgTexel.beginElement();
        VgTexel.drawImage(texelBitmap,x,y,width,height, srcX, srcY,srcWidth, srcHeight);
        VgTexel.endElement();
    }

    @Override
    public void drawBitmap(Rect rect, Rect source, Bitmap bitmap, Paint paint) {
        drawBitmap(rect.left, rect.top, rect.width(), rect.height(), source.left, source.top, source.width(), source.height(),bitmap, paint);
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