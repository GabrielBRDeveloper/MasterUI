package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.C;
import br.nullexcept.mux.graphics.Color;
import br.nullexcept.mux.graphics.Paint;
import br.nullexcept.mux.graphics.Path;
import br.nullexcept.mux.graphics.Point;
import br.nullexcept.mux.utils.Log;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGLES2;

import static org.lwjgl.nanovg.NanoVG.*;

class VgTexel {
    private static long globalContext = 0;
    private static final Paint globalPaint = new Paint();
    private static final NVGColor globalColor = NVGColor.create();
    private static final NVGPaint nvgPaint = NVGPaint.create();
    private static final NVGPaint tmpPaint = NVGPaint.create();
    private static float alpha = 1.0f;
    private static final Point VIEWPORT = new Point();

    public static void initialize(){
        try {
            globalContext = NanoVGGLES2.nvgCreate(NanoVGGLES2.NVG_ANTIALIAS);
            globalPaint.setTextSize(-1f);
            C.VG_CONTEXT = globalContext;
            C.BITMAP_FACTORY = new TexelBitmapFactory();
            GLShaderList.build();
        } catch (Throwable e){
            Log.log("TexelAPI", "Error on initialize nanovg texel.");
            Log.log("TexelAPI", e);
            throw new RuntimeException("CORE ERROR");
        }
    }

    @Deprecated
    public static long getContext() {
        return globalContext;
    }

    public static void applyPaint(Paint paint){
        globalPaint.from(paint);

        nvgFontFaceId(globalContext, paint.getTypeface().hashCode());
        setColor(paint.getColor());
        setTextSize(paint.getTextSize());

        nvgPaint.innerColor(globalColor);
        nvgPaint.outerColor(globalColor);

        nvgFillPaint(globalContext, nvgPaint);
        nvgStrokeColor(globalContext, globalColor);
    }

    private static void setTextSize(float textSize) {
        globalPaint.setTextSize(textSize);
        nvgFontSize(globalContext, globalPaint.getTextSize());
        nvgTextLetterSpacing(globalContext,0.0f);
    }

    public static void setColor(int color){
        globalPaint.setColor(color);

        NanoVG.nvgRGBAf(
                Color.red(color) / 255.0f,
                Color.green(color) / 255.0f,
                Color.blue(color) / 255.0f,
                (Color.alpha(color) / 255.0f) * alpha,
                globalColor
        );
    }

    public static Paint getPaint() {
        return globalPaint;
    }

    private static void fill(){
        switch (globalPaint.getMode()){
            case FILL:
                nvgFill(globalContext);
                break;
            case STROKE:
                nvgStroke(globalContext);
                break;
            default:
                nvgFill(globalContext);
                nvgStroke(globalContext);
                break;
        }
    }

    public static void drawPath(Path path) {
        nvgBeginPath(globalContext);

        for (int i = 0; i < path.length(); i++) {
            nvgPathWinding(globalContext, NVG_HOLE);
            Path.Segment seg = path.segment(i);
            nvgMoveTo(globalContext, seg.beginX(), seg.beginY());
            for (int x = 0; x < seg.partCount(); x++) {
                float[] curves = seg.part(x);
                nvgBezierTo(globalContext, curves[0], curves[1], curves[2], curves[3], curves[4], curves[5]);
            }
            if (seg.closed()) {
                nvgLineTo(globalContext,seg.beginX(), seg.beginY());
            }
        }

        nvgClosePath(globalContext);
        fill();

    }

    public static void drawRoundedRect(int x, int y, int width, int height, int radius){
        radius = Math.min(Math.max(width,height), radius);
        nvgRoundedRect(globalContext, x,y,width,height,radius);
        fill();
    }

    public static void drawEllipse(int x, int y, int width, int height){
        int mw = width/2;
        int mh = height/2;
        nvgEllipse(globalContext, x+mw, y+mh, mw, mh);
        fill();
    }

    public static void drawRect(int x, int y, int width, int height){
        nvgRect(globalContext, x, y, width, height);
        fill();
    }

    public static void beginElement(){
        nvgBeginPath(globalContext);
    }

    public static void endElement(){
        nvgClosePath(globalContext);
    }

    public static void destroy(){
        NanoVGGLES2.nvgDelete(globalContext);
    }

    public static void beginFrame(int w, int h) {
        nvgBeginFrame(globalContext, w,h,1.0f);
        VIEWPORT.set(w,h);
    }

    public static void endFrame() {
        nvgEndFrame(globalContext);
        nvgResetTransform(globalContext);
        nvgReset(globalContext);
        nvgResetScissor(globalContext);
        alpha = 1.0f;
    }

    public static void drawImage(TexelBitmap image, float destX, float destY, float destW, float destH, float srcX, float srcY, float srcW, float srcH) {
        float aw = image.getWidth() / srcW;
        float ah = image.getHeight() / srcH;

        float imgH = destH * ah;
        float imgW = destW *= aw;

        float imgX = destX - ((srcX / image.getWidth()) * imgW);
        float imgY = destY - ((srcY / image.getHeight()) * imgH);

        nvgImagePattern(globalContext, imgX, imgY, imgW, imgH, 0, image.id(), alpha, tmpPaint);
        nvgRect(globalContext, destX, destY, destW, destH);
        nvgFillPaint(globalContext, tmpPaint);
        nvgFill(globalContext);
        nvgFillPaint(globalContext, nvgPaint);
    }

    public static void setAlpha(float alpha) {
        VgTexel.alpha = alpha;
    }

    public static void drawText(int x, int y, CharSequence line) {
        nvgText(globalContext, x,y, line);
    }

    public static void rotate(float angle){
        nvgRotate(globalContext, angle);
    }

    public static void move(int x, int y) {
        nvgTranslate(globalContext, x, y);
    }
}
