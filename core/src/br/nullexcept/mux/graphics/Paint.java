package br.nullexcept.mux.graphics;

import br.nullexcept.mux.graphics.fonts.FontMetrics;
import br.nullexcept.mux.graphics.fonts.Typeface;

public class Paint {
    private Typeface typeface;
    private int flags;
    private int color = Color.WHITE;
    private float textSize = 18.0f;
    private float strokeWidth = 0.0f;
    private FontMetrics metrics;
    private Mode mode = Mode.FILL;

    public Paint(){
        this(0);
    }

    public Paint(int flags){
        this.flags = flags;
        setTypeface(Typeface.DEFAULT);
        setTextSize(18.0f);
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode){
        this.mode = mode;
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
        if (typeface != null)
        this.metrics = typeface.getMetricsFor(this);
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

    public void from(Paint paint){
        color = paint.color;
        mode = paint.mode;
        textSize = paint.textSize;
        flags = paint.flags;
        typeface = paint.typeface;
        strokeWidth = paint.strokeWidth;
    }

    public enum Mode {
        FILL,
        STROKE,
        FILL_AND_STROKE
    }
}
