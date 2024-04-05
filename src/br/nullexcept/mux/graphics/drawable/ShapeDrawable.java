package br.nullexcept.mux.graphics.drawable;

import br.nullexcept.mux.graphics.Canvas;
import br.nullexcept.mux.graphics.Drawable;
import br.nullexcept.mux.graphics.Paint;
import br.nullexcept.mux.graphics.Rect;
import br.nullexcept.mux.graphics.shape.Shape;

public class ShapeDrawable extends Drawable {
    private Shape shape;

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
        Rect bounds = getBounds();
        if (shape != null){
            canvas.translate(bounds.left, bounds.top);
            shape.resize(bounds.width(), bounds.height());
            shape.draw(canvas,paint);
            canvas.translate(-bounds.left, -bounds.top);
        }
    }
}
