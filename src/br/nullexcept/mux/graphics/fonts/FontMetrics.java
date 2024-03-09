package br.nullexcept.mux.graphics.fonts;

import br.nullexcept.mux.graphics.Paint;

public abstract class FontMetrics {
    private final Paint paint;

    protected FontMetrics(Paint paint){
        this.paint = paint;
    }

    public float getAscent(){
        return paint.getTypeface().ascent * scale();
    }

    public float getDescent(){
        return paint.getTypeface().descent * scale();
    }

    public float getLineHeight() {
        return  paint.getTypeface().lineHeight * scale();
    }

    public float measureChar(char ch){
        return paint.getTypeface().measureChar(ch) * scale();
    }

    private float scale(){
        return (paint.getTextSize()/Typeface.SCALE);
    }

    public float measureText(CharSequence line){
        float width = 0;
        Typeface typeface = paint.getTypeface();
        float scale = scale();
        for (int i = 0; i < line.length(); i++){
            width += typeface.measureChar(line.charAt(i)) * scale;
        }
        return width;
    }
}
