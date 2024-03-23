package br.nullexcept.mux.graphics.shape;

import br.nullexcept.mux.graphics.Canvas;
import br.nullexcept.mux.graphics.Paint;

public class RectShape extends Shape {
    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawRect(0,0, getWidth(), getHeight(), paint);
    }
}
