package br.nullexcept.mux.graphics;

public abstract class Drawable {
    private final Rect bounds = new Rect();
    public abstract void draw(Canvas canvas);

    public Rect getBounds() {
        return bounds;
    }

    public void setBounds(Rect rect){
        bounds.set(rect);
    }
}
