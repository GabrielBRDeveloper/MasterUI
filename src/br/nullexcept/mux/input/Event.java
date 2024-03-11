package br.nullexcept.mux.input;

public class Event {
    private final InputDevice device;

    public Event(InputDevice device) {
        this.device = device;
    }

    public InputDevice getDevice() {
        return device;
    }
}
