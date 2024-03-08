package br.nullexcept.mux.renderer.texel;

import br.nullexcept.mux.C;
import br.nullexcept.mux.graphics.Color;
import br.nullexcept.mux.graphics.Paint;
import br.nullexcept.mux.graphics.fonts.TypefaceFactory;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVGGLES2;
import static org.lwjgl.nanovg.NanoVG.*;

class VgTexel {
    private static long globalContext = 0;
    private static final Paint globalPaint = new Paint();
    private static final NVGColor globalColor = NVGColor.create();

    public static void initialize(){
        globalPaint.setTextSize(-1f);
        globalContext = NanoVGGLES2.nnvgCreate(NanoVGGLES2.NVG_ANTIALIAS);
        C.VG_CONTEXT = globalContext;
        TypefaceFactory.createDefaults();
    }

    @Deprecated
    public static long getContext() {
        return globalContext;
    }

    public static void applyPaint(Paint paint){
        nvgFontFaceId(globalContext, paint.getTypeface().hashCode());
        setColor(paint.getColor());
        setTextSize(paint.getTextSize());

        nvgFillColor(globalContext, globalColor);
        nvgStrokeColor(globalContext, globalColor);
        nvgStrokeWidth(globalContext, paint.getStrokeWidth());
    }

    private static void setTextSize(float textSize) {
        globalPaint.setTextSize(textSize);
        nvgFontSize(globalContext, globalPaint.getTextSize());
    }

    public static void setColor(int color){
        globalPaint.setColor(color);

        globalColor.a(Color.alpha(color)/255.0f);
        globalColor.r(Color.red(color)/255.0f);
        globalColor.g(Color.blue(color)/255.0f);
        globalColor.b(Color.green(color)/255.0f);
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
        nvgBeginFrame(globalContext, w,h, 1.0f);
    }

    public static void endFrame() {
        nvgEndFrame(globalContext);
    }

    public static void drawText(int x, int y, String line) {
        nvgText(globalContext, x,y, line);
    }
}
