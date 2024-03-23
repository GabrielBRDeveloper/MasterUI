package br.nullexcept.mux.graphics.drawable;

import br.nullexcept.mux.graphics.Canvas;
import br.nullexcept.mux.graphics.Drawable;

public class ColorDrawable extends Drawable {
    private int color;

    public ColorDrawable(int color){
        this.color = color;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(color);
    }

    public void setColor(int color) {
        this.color = color;
    }
}
