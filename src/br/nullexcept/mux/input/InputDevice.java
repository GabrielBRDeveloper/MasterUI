package br.nullexcept.mux.input;

public class InputDevice {
    private Type type;

    public Type getType() {
        return type;
    }

    enum Type {
        KEYBOARD,
        MOUSE,
        JOYSTICK,
        TOUCH,
        UNKNOWN
    }
}
