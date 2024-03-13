package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.C;
import br.nullexcept.mux.graphics.Color;
import br.nullexcept.mux.graphics.Paint;
import br.nullexcept.mux.graphics.fonts.TypefaceFactory;
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

    public static void initialize(){
        try {
            globalContext = NanoVGGLES2.nvgCreate(NanoVGGLES2.NVG_ANTIALIAS);
            globalPaint.setTextSize(-1f);
            C.VG_CONTEXT = globalContext;
            C.BITMAP_FACTORY = new TexelBitmapFactory();
            TypefaceFactory.createDefaults();
            GLShaderList.build();
        } catch (Throwable e){
            System.err.println("Error on initialize nanovg texel.");
            e.printStackTrace(System.out);
            throw new RuntimeException("CORE ERROR");
        }
    }

    @Deprecated
    public static long getContext() {
        return globalContext;
    }

    public static void applyPaint(Paint paint){
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
    }

    public static void setColor(int color){
        globalPaint.setColor(color);

        NanoVG.nvgRGBAf(
                Color.red(color) / 255.0f,
                Color.green(color) / 255.0f,
                Color.blue(color) / 255.0f,
                Color.alpha(color) / 255.0f,
                globalColor
        );
    }

    public static Paint getPaint() {
        return globalPaint;
    }

    public static void drawRect(int x, int y, int width, int height){
        nvgRect(globalContext, x, y, width, height);
        nvgFill(globalContext);
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
    }

    public static void endFrame() {
        nvgEndFrame(globalContext);
        nvgResetTransform(globalContext);
        nvgReset(globalContext);
        nvgResetScissor(globalContext);
    }

    public static void drawImage(TexelBitmap image, float destX, float destY, float destW, float destH, float srcX, float srcY, float srcW, float srcH) {
        float aw = image.getWidth() / srcW;
        float ah = image.getHeight() / srcH;

        float imgH = destH * ah;
        float imgW = destW *= aw;

        float imgX = destX - ((srcX / image.getWidth()) * imgW);
        float imgY = destY - ((srcY / image.getHeight()) * imgH);

        nvgImagePattern(globalContext, imgX, imgY, imgW, imgH, 0, image.id(), 1.0f, tmpPaint);
        nvgRect(globalContext, destX, destY, destW, destH);
        nvgFillPaint(globalContext, tmpPaint);
        nvgFill(globalContext);
        nvgFillPaint(globalContext, nvgPaint);
    }

    public static void drawText(int x, int y, String line) {
        nvgText(globalContext, x,y, line);
    }
}
