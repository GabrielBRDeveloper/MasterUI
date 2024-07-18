package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.graphics.Paint;
import br.nullexcept.mux.graphics.fonts.FontMetrics;

public class TexelFontMetrics extends FontMetrics  {
    private final Paint paint;

    protected TexelFontMetrics(Paint paint){
        this.paint = paint;
    }
    
    private TexelFont typeface() {
        return ((TexelFont)paint.getTypeface());
    }

    public float getAscent(){
        return typeface().ascent * scale();
    }
    public float getDescent(){
        return typeface().descent * scale();
    }

    public float getLineHeight() {
        return  typeface().lineHeight * scale();
    }

    public float measureChar(char ch){
        return typeface().measureChar(ch) * scale();
    }

    private float scale(){
        return (paint.getTextSize()/ TexelFont.SCALE);
    }
    public float measureText(CharSequence line){
        float width = 0;
        TexelFont typeface = typeface();
        float scale = scale();
        for (int i = 0; i < line.length(); i++){
            width += typeface.measureChar(line.charAt(i)) * scale;
        }
        return width;
    }
}
