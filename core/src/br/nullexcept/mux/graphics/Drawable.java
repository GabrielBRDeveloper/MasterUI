package br.nullexcept.mux.graphics;

public abstract class Drawable {
    private final StateList state = new StateList();
    private final Rect bounds = new Rect();
    protected final Paint paint = new Paint();

    public abstract void draw(Canvas canvas);

    public Rect getBounds() {
        return bounds;
    }

    public void setBounds(Rect rect){
        bounds.set(rect);
    }

    public boolean setState(StateList state) {
        this.state.setTo(state);
        return false;
    }

    public int getWidth(){
        return 1;
    }

    public int getHeight(){
        return 1;
    }

    public StateList getState() {
        return state;
    }
}
