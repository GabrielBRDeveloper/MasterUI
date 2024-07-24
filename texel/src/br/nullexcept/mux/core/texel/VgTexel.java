package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.C;
import br.nullexcept.mux.graphics.*;
import br.nullexcept.mux.utils.Log;
import org.lwjgl.nanovg.*;
import org.lwjgl.opengles.GLES20;

import static org.lwjgl.nanovg.NanoVG.*;

class VgTexel {
    private static long currentContext = 0;
    private static long globalContext = 0;
    private static long clipContext = 0;
    private static final Paint globalPaint = new Paint();
    private static final NVGColor globalColor = NVGColor.create();
    private static final NVGPaint nvgPaint = NVGPaint.create();
    private static final NVGPaint tmpPaint = NVGPaint.create();
    private static float alpha = 1.0f;
    private static final Size drawViewport = new Size();
    private static GLFramebuffer clipFbo;

    public static void initialize(){
        try {
            globalContext = NanoVGGLES2.nvgCreate(NanoVGGLES2.NVG_ANTIALIAS);
            clipContext = NanoVGGLES2.nvgCreate(NanoVGGLES2.NVG_ANTIALIAS);
            globalPaint.setTextSize(-1f);
            C.VG_CONTEXT = globalContext;
            C.TYPEFACE_FACTORY = new TexelFontFactory();
            C.BITMAP_FACTORY = new TexelBitmapFactory();
            GLShaderList.build();

            clipFbo = new GLFramebuffer(1,1);
            currentContext = globalContext;
        } catch (Throwable e){
            Log.log("TexelAPI", "Error on initialize nanovg texel.");
            Log.log("TexelAPI", e);
            throw new RuntimeException("CORE ERROR");
        }
    }

    @Deprecated
    public static long getContext() {
        return currentContext;
    }

    public static void applyPaint(Paint paint){
        globalPaint.from(paint);

        nvgFontFaceId(currentContext, paint.getTypeface().hashCode());
        setColor(paint.getColor());
        setTextSize(paint.getTextSize());

        nvgPaint.innerColor(globalColor);
        nvgPaint.outerColor(globalColor);

        nvgFillPaint(currentContext, nvgPaint);
        nvgStrokeColor(currentContext, globalColor);
    }


    public static void beginClip() {
        setCurrentContext(clipContext);
        clipFbo.resize(drawViewport.width, drawViewport.height);
        clipFbo.bind();
        clipFbo.clear(Color.TRANSPARENT);
        beginFrame(drawViewport.width, drawViewport.height);
    }

    public static void endClip(int srcFBO) {
        endFrame();
        clipFbo.unbind();
        GLES.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, srcFBO);
        setCurrentContext(globalContext);
        flushFrame();
        GLTexel.drawTextureClip(0,0, drawViewport.width, drawViewport.height,clipFbo.getTexture().getTexture());
    }

    private static void setTextSize(float textSize) {
        globalPaint.setTextSize(textSize);
        nvgFontSize(currentContext, globalPaint.getTextSize());
        nvgTextLetterSpacing(currentContext,0.0f);
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
                nvgFill(currentContext);
                break;
            case STROKE:
                nvgStroke(currentContext);
                break;
            default:
                nvgFill(currentContext);
                nvgStroke(currentContext);
                break;
        }
    }

    public static void setCurrentContext(long context) {
        currentContext = context;
    }

    public static void drawPath(Path path) {
        nvgBeginPath(currentContext);

        for (int i = 0; i < path.length(); i++) {
            nvgPathWinding(currentContext, NVG_HOLE);
            Path.Segment seg = path.segment(i);
            nvgMoveTo(currentContext, seg.beginX(), seg.beginY());
            for (int x = 0; x < seg.partCount(); x++) {
                float[] curves = seg.part(x);
                nvgBezierTo(currentContext, curves[0], curves[1], curves[2], curves[3], curves[4], curves[5]);
            }
            if (seg.closed()) {
                nvgLineTo(currentContext,seg.beginX(), seg.beginY());
            }
        }

        nvgClosePath(currentContext);
        fill();

    }

    public static void drawRoundedRect(int x, int y, int width, int height, float rl, float rt, float rr, float rb){
        if (rl == rr && rr == rt && rt == rb) {
            nvgRoundedRect(currentContext, x, y, width, height, rr);
        } else {
            nvgRoundedRectVarying(currentContext, x, y, width, height, rl, rr, rb, rt);
        }
        fill();
    }

    public static void drawEllipse(int x, int y, int width, int height){
        int mw = width/2;
        int mh = height/2;
        nvgEllipse(currentContext, x+mw, y+mh, mw, mh);
        fill();
    }

    public static void drawRect(int x, int y, int width, int height){
        nvgRect(currentContext, x, y, width, height);
        fill();
    }

    public static void beginElement(){
        nvgBeginPath(currentContext);
    }

    public static void endElement(){
        nvgClosePath(currentContext);
    }

    public static void destroy(){
        NanoVGGLES2.nvgDelete(currentContext);
        NanoVGGLES2.nvgDelete(clipContext);
    }

    public static void beginFrame(int w, int h) {
        nvgBeginFrame(currentContext, w,h,1.0f);
        drawViewport.set(w,h);
    }

    public static void flushFrame() {
        nvgEndFrame(currentContext);
        nvgBeginFrame(currentContext, drawViewport.width, drawViewport.height, 1.0f);
    }

    public static void endFrame() {
        nvgEndFrame(currentContext);
        nvgResetTransform(currentContext);
        nvgReset(currentContext);
        nvgResetScissor(currentContext);
        alpha = 1.0f;
    }

    public static void drawImage(TexelBitmap image, float destX, float destY, float destW, float destH, float srcX, float srcY, float srcW, float srcH) {
        drawImage(image.id(), image.getWidth(), image.getHeight(), destX, destY, destW, destH, srcX, srcY, srcW, srcH);
    }

    public static void drawImage(int img, int originW, int originH, float destX, float destY, float destW, float destH, float srcX, float srcY, float srcW, float srcH) {
        float aw = originW / srcW;
        float ah = originH / srcH;

        float imgH = destH * ah;
        float imgW = destW * aw;

        float imgX = destX - ((srcX / originW) * imgW);
        float imgY = destY - ((srcY / originH) * imgH);

        nvgImagePattern(currentContext, imgX, imgY, imgW, imgH, 0, img, alpha, tmpPaint);
        nvgRect(currentContext, destX, destY, destW, destH);
        nvgFillPaint(currentContext, tmpPaint);
        nvgFill(currentContext);
        nvgFillPaint(currentContext, nvgPaint);
    }

    public static void setAlpha(float alpha) {
        VgTexel.alpha = alpha;
    }

    public static void drawText(int x, int y, CharSequence line) {
        nvgText(currentContext, x,y, line);
    }

    public static void rotate(float angle){
        nvgRotate(currentContext, angle);
    }

    public static void move(int x, int y) {
        nvgTranslate(currentContext, x, y);
    }
}
