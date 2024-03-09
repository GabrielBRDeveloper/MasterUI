package br.nullexcept.mux.graphics;

import br.nullexcept.mux.graphics.fonts.FontMetrics;
import br.nullexcept.mux.graphics.fonts.Typeface;

public class Paint {
    private Typeface typeface;
    private int flags;
    private int color = Color.WHITE;
    private float textSize = 18.0f;
    private float strokeWidth = 0.0f;
    private final FontMetrics metrics;

    public Paint(){
        this(0);
    }

    public Paint(int flags){
        this.flags = flags;
        setTypeface(Typeface.DEFAULT);
        setTextSize(18.0f);
        metrics = new PaintMetrics(this);
    }

    public int getFlags() {
        return flags;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public float getTextSize() {
        return textSize;
    }

    public int getColor() {
        return color;
    }

    public Typeface getTypeface() {
        return typeface == null ? Typeface.DEFAULT : typeface;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public FontMetrics getFontMetrics() {
        return metrics;
    }

    private class PaintMetrics extends FontMetrics {
        protected PaintMetrics(Paint paint) {
            super(paint);
        }
    }
}
