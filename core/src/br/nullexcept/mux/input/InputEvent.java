package br.nullexcept.mux.input;

public abstract class InputEvent extends Event {
    public abstract InputDevice getDevice();

    public InputSource getSource() {
        return getDevice().getSource();
    }

    public abstract long getDownTime();
}
