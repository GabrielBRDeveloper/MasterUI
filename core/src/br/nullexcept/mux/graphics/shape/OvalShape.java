package br.nullexcept.mux.graphics.shape;

import br.nullexcept.mux.graphics.Canvas;
import br.nullexcept.mux.graphics.Paint;

public class OvalShape extends Shape {
    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawEllipse(bounds, paint);
    }
}
