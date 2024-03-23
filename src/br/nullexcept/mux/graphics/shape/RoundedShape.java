package br.nullexcept.mux.graphics.shape;

import br.nullexcept.mux.graphics.Canvas;
import br.nullexcept.mux.graphics.Paint;

public class RoundedShape extends Shape {
    private int radius = 0;

    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawRoundRect(0,0,getWidth(),getHeight(), radius,paint);
    }
}
