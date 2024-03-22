package br.nullexcept.mux.input;

public class Event {
    public static final int NONE_TARGET = -1;
    private int target = -1;

    public void setTarget(int target) {
        this.target = target;
    }

    public int getTarget() {
        return target;
    }
}
