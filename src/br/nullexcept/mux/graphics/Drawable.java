package br.nullexcept.mux.graphics;

public abstract class Drawable {
    private DrawableState state;
    private final Rect bounds = new Rect();
    public abstract void draw(Canvas canvas);

    public Rect getBounds() {
        return bounds;
    }

    public void setBounds(Rect rect){
        bounds.set(rect);
    }

    public boolean setState(DrawableState state) {
        this.state = state;
        return false;
    }

    public DrawableState getState() {
        return state;
    }
}
