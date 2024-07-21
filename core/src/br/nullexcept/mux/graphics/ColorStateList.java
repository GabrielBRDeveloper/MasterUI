package br.nullexcept.mux.graphics;

import java.util.ArrayList;

public class ColorStateList {
    private final ArrayList<ColorState> colors = new ArrayList<>();
    private final StateList state = new StateList();
    private final int defaultColor;
    private int color;

    public ColorStateList() {
        this(Color.TRANSPARENT);
    }

    public ColorStateList(int defaultColor) {
        this.defaultColor = defaultColor;
        color = defaultColor;
    }

    public boolean setState(StateList state) {
        int color = this.color;
        this.state.setTo(state);
        this.color = getCurrent();

        return color != this.color;
    }

    public void add(int color, StateList state) {
        this.colors.add(new ColorState(color, state));
        this.color = getCurrent();
    }

    private int getCurrent() {
        int color = defaultColor;
        for (ColorState item: colors) {
            if (!item.state.hasConflict(this.state)) {
                color = item.color;
                break;
            }
        }
        return color;
    }

    public int getColor() {
        return color;
    }

    private static class ColorState {
        private final int color;
        private final StateList state;

        private ColorState(int color, StateList state) {
            this.color = color;
            this.state = state;
        }
    }
}
