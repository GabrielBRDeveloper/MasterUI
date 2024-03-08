package br.nullexcept.mux.renderer.texel;

import br.nullexcept.mux.graphics.Canvas;
import br.nullexcept.mux.graphics.Paint;
import br.nullexcept.mux.graphics.Point;
import br.nullexcept.mux.graphics.Rect;
import br.nullexcept.mux.hardware.GLES;
import br.nullexcept.mux.renderer.texel.surface.GLFramebuffer;
import org.lwjgl.opengles.GLES20;

public class CanvasTexel implements Canvas {
    private static long CURRENT_CANVAS = -1;
    private final GLFramebuffer framebuffer;
    private final long id = (long) (Math.random()*Long.MAX_VALUE);
    private final Point translation = new Point();

    public CanvasTexel(int width, int height) {
        this.framebuffer = new GLFramebuffer(width, height);
    }

    public void begin(){
        if (CURRENT_CANVAS != id){
            VgTexel.endFrame();
            framebuffer.bind();
            VgTexel.beginFrame(framebuffer.getWidth(), framebuffer.getHeight());
        }
    }

    @Override
    public void reset() {
        GLES.glClearColor(0,0,0,0);
        GLES.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
        translation.set(0,0);
    }

    @Override
    public void end() {
        VgTexel.endFrame();
        framebuffer.unbind();
    }

    @Override
    public void drawColor(int color) {
        VgTexel.setColor(color);
        drawRect(0,0, getWidth(),getHeight(), VgTexel.getPaint());
    }

    @Override
    public void drawRect(int left, int top, int right, int bottom, Paint paint) {
        VgTexel.beginElement();
        VgTexel.applyPaint(paint);
        VgTexel.drawRect(left,top,right-left, bottom-top);
        VgTexel.endElement();
    }

    @Override
    public void drawRect(Rect rect, Paint paint) {
        drawRect(rect.left, rect.top, rect.right, rect.bottom, paint);
    }

    @Override
    public void drawText(String text, int x, int y, Paint paint) {
        if (text == null){
            return;
        }
        String[] lines = text.split("\n");
        VgTexel.beginElement();
        VgTexel.applyPaint(paint);
        x += translation.x;
        y += translation.y;
        for (String line: lines) {
            VgTexel.drawText(x, y, line);
            y += paint.getTextSize();
        }
        VgTexel.endElement();
    }

    @Override
    public void translate(int x, int y) {
        translation.x += x;
        translation.y += y;
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


    public static void initialize(){
        VgTexel.initialize();
    }

    public static void destroy(){
        VgTexel.destroy();
    }
}