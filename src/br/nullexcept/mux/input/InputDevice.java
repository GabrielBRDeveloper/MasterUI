package br.nullexcept.mux.input;

public abstract class InputDevice {
    private Type type;

    public abstract Type getType();

    enum Type {
        KEYBOARD,
        MOUSE,
        JOYSTICK,
        TOUCH,
        UNKNOWN
    }
}
