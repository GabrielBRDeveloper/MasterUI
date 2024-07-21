package br.nullexcept.mux.graphics;

public class PointF {
    public float x = 0;
    public float y = 0;

    public PointF(){

    }
    public PointF(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(Point point) {
        set(point.x, point.y);
    }

    public void set(PointF point) {
        set(point.x, point.y);
    }
}
