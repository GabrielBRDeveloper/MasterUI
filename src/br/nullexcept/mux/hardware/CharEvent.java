package br.nullexcept.mux.hardware;

import br.nullexcept.mux.input.InputDevice;
import br.nullexcept.mux.input.InputEvent;

public final class CharEvent extends InputEvent {
    private final char character;
    private final long downTime;
    private final InputDevice source;

    public CharEvent(char character, long downTime, InputDevice source) {
        this.character = character;
        this.downTime = downTime;
        this.source = source;
    }

    @Override
    public InputDevice getSource() {
        return source;
    }

    public long getDownTime() {
        return downTime;
    }

    public char getCharacter() {
        return character;
    }
}
