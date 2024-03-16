package br.nullexcept.mux.input;

import br.nullexcept.mux.graphics.Point;

public abstract class MotionEvent extends InputEvent {
    public static final int ACTION_DOWN = 2;
    public static final int ACTION_UP = 4;
    public static final int ACTION_MOVE = 8;
    public static final int ACTION_PRESSED = ACTION_DOWN | ACTION_UP;

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

    public abstract int getAction();

    protected void resetTransform(){
        transform.set(0,0);
    }
}
