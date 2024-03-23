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

    public int getWidth(){
        return 1;
    }

    public int getHeight(){
        return 1;
    }

    public DrawableState getState() {
        return state;
    }
}
