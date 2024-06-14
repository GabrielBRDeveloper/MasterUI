package br.nullexcept.mux.graphics.shape;

import br.nullexcept.mux.graphics.Canvas;
import br.nullexcept.mux.graphics.Paint;
import br.nullexcept.mux.graphics.Rect;

public class RoundedShape extends Shape {
    private final Rect radius = new Rect();

    public void setRadius(int radius) {
        this.radius.set(radius,radius,radius,radius);
    }

    public void setRadius(Rect radius) {
        this.radius.set(radius);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawRoundRect(bounds, radius,paint);
    }
}
