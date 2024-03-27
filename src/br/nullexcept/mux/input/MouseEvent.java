package br.nullexcept.mux.input;

import br.nullexcept.mux.graphics.Point;

public abstract class MouseEvent extends MotionEvent {
    public static final int LEFT_BUTTON = 0;
    public static final int MIDDLE_BUTTON = 1;
    public static final int RIGHT_BUTTON = 2;
    public static final int BUTTON_NONE = 0;

    public abstract int getButton();
    public abstract Point getScroll();

}
