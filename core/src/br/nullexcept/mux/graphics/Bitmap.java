package br.nullexcept.mux.graphics;

public interface Bitmap {
    int getWidth();
    int getHeight();
    Format getFormat();
    boolean isValid();
    void dispose();

    public static enum Format {
        ARGB,
        RGB,
        RGB565
    }
}
