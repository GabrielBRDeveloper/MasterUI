package br.nullexcept.mux.graphics;

public interface Bitmap {
    int getWidth();
    int getHeight();
    int getPixel(int x, int y);
    void setPixel(int x, int y);
    Format getFormat();
    boolean isValid();
    void dispose();

    public static enum Format {
        ARGB,
        RGB,
        RGB565
    }
}
