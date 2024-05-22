package br.nullexcept.mux.graphics.fonts;

import br.nullexcept.mux.C;
import br.nullexcept.mux.utils.Log;
import org.lwjgl.nanovg.NVGGlyphPosition;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.UUID;

public class Typeface {
    protected static final float SCALE = 1024.0f;

    public static final int STYLE_NORMAL = 0;
    public static final int STYLE_BOLD = 1;
    public static final int STYLE_ITALIC = 2;

    public static Typeface DEFAULT;

    protected final float ascent;
    protected final float descent;
    protected final float lineHeight;
    protected final ByteBuffer buffer;

    private final int font;
    private final String id = UUID.randomUUID().toString();
    private final short[] bounds = new short[1024*16]; //64KB of buffer for store characters bounds

    Typeface(ByteBuffer buffer){
        this.buffer = buffer;
        long context = C.VG_CONTEXT;
        font = NanoVG.nvgCreateFontMem(context, id,buffer,false);
        NanoVG.nvgFontFaceId(context, font);
        NanoVG.nvgTextLetterSpacing(context, 0.0f);
        NanoVG.nvgFontSize(context, SCALE);
        NanoVG.nvgFontBlur(context,0);
        float[] ascent = new float[1];
        float[] descent = new float[1];
        float[] lineHeight = new float[1];
        NanoVG.nvgTextMetrics(context, ascent,descent,lineHeight);
        this.ascent = ascent[0];
        this.descent = descent[0];
        this.lineHeight = lineHeight[0];
        NVGGlyphPosition.create();
        NVGGlyphPosition.Buffer b = NVGGlyphPosition.create(1);
        for (int i = 0; i < this.bounds.length; i++){
            String character = String.valueOf((char)i);
            NanoVG.nvgTextGlyphPositions(context,0,0,character,b);
            this.bounds[i] = (short) ((short) Math.abs(b.maxx()) + Math.abs(b.minx()));
        }
    }

    protected int measureChar(char ch){
        if (ch > bounds.length){
            Log.log("Typeface","INVALID MEASURE OUTBOUNDS CHAR: "+(int)ch);
            ch = 'Z';
        }
        switch (ch){
            case '\t':
            case '\f':
                return bounds[' '];
            case '\n':
            case '\r':
                return 0;
        }
        return bounds[ch];
    }

    public int hashCode(){
        return font;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
