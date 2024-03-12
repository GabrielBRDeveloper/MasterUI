package br.nullexcept.mux.graphics.drawable;

import br.nullexcept.mux.graphics.Bitmap;
import br.nullexcept.mux.graphics.Canvas;
import br.nullexcept.mux.graphics.Drawable;
import br.nullexcept.mux.graphics.Paint;

public class BitmapDrawable extends Drawable {
    private final Bitmap bitmap;
    private final Paint paint = new Paint();
    public BitmapDrawable(Bitmap bitmap){
        this.bitmap = bitmap;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(getBounds(),bitmap,paint);
    }
}
