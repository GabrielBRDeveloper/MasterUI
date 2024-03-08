package br.nullexcept.mux.graphics;

public interface Canvas {
    void drawColor(int color);
    void drawRect(int left, int top, int right, int bottom, Paint paint);
    void drawRect(Rect rect, Paint paint);
    void drawText(String text, int x, int y, Paint paint);
    void translate(int x, int y);

    void begin();
    void reset();
    void end();
    //void drawBitmap(Bitmap bitmap, Paint paint);

    void dispose();

    int getWidth();
    int getHeight();
}