package br.nullexcept.mux.graphics.shape;

import br.nullexcept.mux.graphics.Canvas;
import br.nullexcept.mux.graphics.Paint;
import br.nullexcept.mux.graphics.Rect;

public abstract class Shape {
    protected final Rect bounds = new Rect();

    public void resize(int width, int height) {
        bounds.setSize(width, height);
    }

    public void setPosition(int x, int y) {
        bounds.setPosition(x,y);
    }

    public int getWidth() {
        return bounds.width();
    }

    public int getHeight() {
        return bounds.height();
    }

    public abstract void draw(Canvas canvas, Paint paint);
}
