package br.nullexcept.mux.graphics;

public interface Canvas {
    void drawColor(int color);
    default void drawRect(float left, float top, float right, float bottom, Paint paint){
        drawRect(Math.round(left), Math.round(top), Math.round(right), Math.round(bottom), paint);
    }
    void drawRect(int left, int top, int right, int bottom, Paint paint);
    void drawRect(Rect rect, Paint paint);
    void drawText(CharSequence text, int x, int y, Paint paint);
    void translate(int x, int y);
    void rotate(float angle);

    void drawBitmap(Rect rect, Bitmap bitmap, Paint paint);
    void drawBitmap(int x, int y, int width, int height, Bitmap bitmap, Paint paint);
    void drawBitmap(int x, int y, int width, int height, int srcX, int srcY, int srcWidth, int srcHeight, Bitmap bitmap, Paint paint);
    void drawBitmap(Rect rect, Rect source, Bitmap bitmap, Paint paint);
    void drawBitmap(int x, int y, Bitmap bitmap, Paint paint);

    void drawEllipse(int left, int top, int right, int bottom, Paint paint);
    default void drawEllipse(Rect rect, Paint paint){
        drawEllipse(rect.left,rect.top, rect.right, rect.bottom, paint);
    }

    void drawRoundRect(int left, int top, int right, int bottom, int radius, Paint paint);
    default void drawRoundRect(Rect rect, int radius, Paint paint){
        drawRoundRect(rect.left, rect.top, rect.right, rect.bottom, radius, paint);
    }

    void drawPath(Path path, int x, int y, Paint paint);

    void begin();
    void reset();
    void end();
    //void drawBitmap(Bitmap bitmap, Paint paint);

    void dispose();

    int getWidth();
    int getHeight();
}