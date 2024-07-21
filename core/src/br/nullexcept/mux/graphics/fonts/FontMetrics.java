package br.nullexcept.mux.graphics.fonts;

public abstract class FontMetrics {
    protected FontMetrics(){}

    public abstract float getAscent();
    public abstract float getDescent();
    public abstract float getLineHeight();
    public abstract float measureChar(char ch);
    public abstract float measureText(CharSequence line);
}
