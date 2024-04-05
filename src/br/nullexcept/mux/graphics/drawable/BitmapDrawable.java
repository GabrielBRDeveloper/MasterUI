package br.nullexcept.mux.graphics.drawable;

import br.nullexcept.mux.graphics.Bitmap;
import br.nullexcept.mux.graphics.Canvas;
import br.nullexcept.mux.graphics.Drawable;
import br.nullexcept.mux.graphics.Paint;

public class BitmapDrawable extends Drawable {
    private final Bitmap bitmap;
    public BitmapDrawable(Bitmap bitmap){
        this.bitmap = bitmap;
    }

    @Override
    public int getWidth() {
        return bitmap.getWidth();
    }

    @Override
    public int getHeight() {
        return bitmap.getHeight();
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(getBounds(),bitmap,paint);
    }
}
