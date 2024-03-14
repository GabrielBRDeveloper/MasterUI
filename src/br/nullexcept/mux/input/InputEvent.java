package br.nullexcept.mux.input;

public abstract class InputEvent extends Event {
    public abstract InputDevice getSource();
    public abstract long getDownTime();
}
