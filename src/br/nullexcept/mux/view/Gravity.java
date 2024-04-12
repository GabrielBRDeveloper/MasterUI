package br.nullexcept.mux.view;

import br.nullexcept.mux.graphics.Rect;

public class Gravity {
    private static final int SHIFT_X = 8;
    private static final int SHIFT_Y = 0;

    private static final int BEFORE = 1;
    private static final int MIDDLE = 2;
    private static final int AFTER = 6;

    public static final int LEFT = BEFORE << SHIFT_X;
    public static final int CENTER_HORIZONTAL = MIDDLE << SHIFT_X;
    public static final int RIGHT = AFTER << SHIFT_X;

    public static final int TOP = BEFORE << SHIFT_Y;
    public static final int CENTER_VERTICAL = MIDDLE << SHIFT_Y;
    public static final int BOTTOM = AFTER << SHIFT_Y;

    public static final int CENTER = CENTER_HORIZONTAL | CENTER_VERTICAL;
    public static final int NO_GRAVITY = LEFT | TOP;

    public static int vertical(int gravity) {
        return (gravity >> SHIFT_Y) & 255;
    }

    public static int horizontal(int gravity) {
        return (gravity >> SHIFT_X) & 255;
    }


    public static int apply(int primaryGravity, int size, int child) {
        switch (primaryGravity) {
            case AFTER:
                return size - child;
            case MIDDLE:
                return (size-child)/2;
        }
        return 0;
    }

    public static void applyGravity(int gravity, int width, int height, int viewportWidth, int viewportHeight, Rect dest) {
        int vertical = vertical(gravity);
        int horizontal = horizontal(gravity);

        dest.top = apply(vertical, viewportHeight, height);
        dest.left = apply(horizontal, viewportWidth, width);

        dest.bottom = dest.top + height;
        dest.right = dest.left + width;
    }
}