package br.nullexcept.mux.graphics.drawable;

import br.nullexcept.mux.graphics.Canvas;
import br.nullexcept.mux.graphics.Drawable;
import br.nullexcept.mux.graphics.Paint;

public class ColorDrawable extends Drawable {

    public ColorDrawable(int color){
        paint.setColor(color);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(getBounds(), paint);
    }

    public void setColor(int color) {
        paint.setColor(color);
    }
}
