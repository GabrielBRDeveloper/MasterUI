package br.nullexcept.mux.graphics.drawable;

import br.nullexcept.mux.graphics.Canvas;
import br.nullexcept.mux.graphics.Drawable;
import br.nullexcept.mux.graphics.Paint;
import br.nullexcept.mux.graphics.shape.Shape;

public class ShapeDrawable extends Drawable {
    private Shape shape;
    private final Paint paint = new Paint();

    public ShapeDrawable(Shape shape){
        this.shape = shape;
    }

    public ShapeDrawable(){
        this(null);
    }

    public void setStrokeSize(float width){
        paint.setStrokeWidth(width);
    }

    public void setColor(int color) {
        paint.setColor(color);
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    @Override
    public void draw(Canvas canvas) {
        if (shape != null){
            shape.resize(getBounds().width(), getBounds().height());
            shape.draw(canvas,paint);
        }
    }
}
