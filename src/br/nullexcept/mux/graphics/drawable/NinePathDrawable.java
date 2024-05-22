package br.nullexcept.mux.graphics.drawable;

import br.nullexcept.mux.graphics.Bitmap;
import br.nullexcept.mux.graphics.Canvas;
import br.nullexcept.mux.graphics.Drawable;
import br.nullexcept.mux.graphics.Rect;

public class NinePathDrawable extends Drawable {
    private Bitmap bitmap;
    private int size = 4;

    public void setBorder(int size) {
        this.size = size;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public void draw(Canvas canvas) {
        if (bitmap != null && bitmap.isValid()) {
            Rect bounds = getBounds();
            canvas.drawBitmap(bounds.left, bounds.top, size,size, 0,0, size,size, bitmap, paint); // LEFT TOP
            canvas.drawBitmap(bounds.right-size, bounds.top, size,size, bitmap.getWidth()-size,0, size,size, bitmap, paint); // RIGHT TOP
            canvas.drawBitmap(bounds.left, bounds.bottom-size, size,size, 0,bitmap.getHeight()-size, size,size, bitmap, paint); // LEFT BOTTOM
            canvas.drawBitmap(bounds.right-size, bounds.bottom-size, size,size, bitmap.getWidth()-size,bitmap.getHeight()-size, size,size, bitmap, paint); // RIGHT BOTTOM

            canvas.drawBitmap(bounds.left+size,bounds.top,bounds.width()-size-size,size,size,0,bitmap.getWidth()-(size*2),size,bitmap,paint); // CENTER TOP
            canvas.drawBitmap(bounds.left+size,bounds.bottom-size,bounds.width()-size-size,size,size,bitmap.getHeight()-size,bitmap.getWidth()-(size*2),size,bitmap,paint); // CENTER BOTTOM
            canvas.drawBitmap(bounds.left,bounds.top+size,size,bounds.height()-size-size,0,size,size,bitmap.getHeight()-(size*2),bitmap,paint); // CENTER LEFT
            canvas.drawBitmap(bounds.right-size,bounds.top+size,size,bounds.height()-size-size,bitmap.getWidth()-size,size,size,bitmap.getHeight()-(size*2),bitmap,paint); // CENTER RIGHT
            canvas.drawBitmap(bounds.left+size,bounds.top+size,bounds.width()-(size*2),bounds.height()-(size*2),size,size,bitmap.getWidth()-(size*2),bitmap.getHeight()-(size*2),bitmap,paint); // CENTER INNER
        }
    }
}
