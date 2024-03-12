package br.nullexcept.mux.input;

import br.nullexcept.mux.graphics.Point;

public abstract class MouseEvent extends InputEvent {
    public static final int LEFT_BUTTON = 0;
    public static final int MIDDLE_BUTTON = 1;
    public static final int RIGHT_BUTTON = 2;
    public static final int ACTION_DOWN = 0;
    public static final int ACTION_UP = 1;


    private final Point transform = new Point();
    public float getX() {
        return (float) getRawX() + transform.x;
    }

    public float getY(){
        return (float) getRawY() + transform.y;
    }

    public void transform(float x, float y){
        transform.x += x;
        transform.y += y;
    }

    public abstract double getRawX();
    public abstract double getRawY();

    public abstract int getButton();
    public abstract int getAction();
    public abstract long getDownTime();

    protected void resetTransform(){
        transform.set(0,0);
    }
}
