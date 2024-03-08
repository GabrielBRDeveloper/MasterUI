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
        return paint.getTypeface().lineHeight * scale();
    }

    public float measureChar(char ch){
        return paint.getTypeface().measureChar(ch) * scale();
    }

    protected float scale(){
        return (paint.getTextSize()/Typeface.SCALE);
    }

    protected void update(){}
}
