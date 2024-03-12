package br.nullexcept.mux.graphics.fonts;

import br.nullexcept.mux.C;
import org.lwjgl.nanovg.NVGGlyphPosition;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

public class Typeface {
    protected static final float SCALE = 128.0f;
    public static Typeface DEFAULT;

    protected final float ascent;
    protected final float descent;
    protected final float lineHeight;
    protected final ByteBuffer buffer;

    private final int font;
    private final String id = UUID.randomUUID().toString();
    private final byte[][] bounds = new byte[1024*8][4]; //32KB of buffer for store characters bounds

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
        float[] bounds = new float[4];
        NVGGlyphPosition.create();
        NVGGlyphPosition.Buffer b = NVGGlyphPosition.create(1);
        ByteBuffer bf = MemoryUtil.memByteBuffer(b.address(),b.sizeof());
        for (int i = 0; i < this.bounds.length; i++){
            String character = String.valueOf((char)i);
            Arrays.fill(bounds,0);
            NanoVG.nvgTextBounds(context,0,lineHeight[0],character,bounds);
            NanoVG.nvgTextGlyphPositions(context,0,0,character,b);
            this.bounds[i][0] = (byte) Math.abs((int)bounds[0]);
            this.bounds[i][1] = (byte) Math.abs((int)bounds[1]);
            this.bounds[i][2] = (byte) ((int)(b.maxx()));
            this.bounds[i][3] = (byte) ((int)bounds[3]);
        }
    }

    protected int measureChar(char ch){
        if (ch > bounds.length){
            ch = 'Z';
        }
        int size = (bounds[ch][2] & 0xFF) + (bounds[ch][0] & 0xFF);
        return size;
    }

    public int hashCode(){
        return font;
    }

    @Override
    public String toString() {
        return id;
    }
}
