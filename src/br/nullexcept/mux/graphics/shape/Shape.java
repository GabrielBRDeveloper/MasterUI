package br.nullexcept.mux.graphics.shape;

import br.nullexcept.mux.graphics.Canvas;
import br.nullexcept.mux.graphics.Paint;

public abstract class Shape {
    private int width;
    private int height;


    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public abstract void draw(Canvas canvas, Paint paint);
}
