package br.nullexcept.mux.input;

public abstract class MouseEvent extends MotionEvent {
    public static final int LEFT_BUTTON = 0;
    public static final int MIDDLE_BUTTON = 1;
    public static final int RIGHT_BUTTON = 2;

    public abstract int getButton();

}
