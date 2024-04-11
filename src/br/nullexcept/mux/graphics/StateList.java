package br.nullexcept.mux.graphics;

import java.util.HashMap;

public class StateList {
    public static final State FOCUSED = State.FOCUSED;
    public static final State PRESSED = State.PRESSED;
    public static final State HOVERED = State.HOVERED;
    public static final State CHECKED = State.CHECKED;
    public static final State CLICKABLE = State.CLICKABLE;

    private final HashMap<State, Boolean> states = new HashMap<>();

    public void set(State state, boolean val) {
        states.put(state, val);
    }

    public boolean has(State state) {
        return states.containsKey(state);
    }

    public boolean get(State state) {
        return has(state) ? states.get(state) : false;
    }

    public boolean hasConflict(StateList state){
        for (State stat: state.states.keySet()){
            if (state.get(stat) != get(stat)){
                return true;
            }
        }
        return false;
    }

    public static State fromName(String name){
        name = name.toUpperCase();
        return State.valueOf(name);
    }

    public void setTo(StateList source) {
        states.clear();
        for (State key: source.states.keySet()){
            states.put(key, source.get(key));
        }
    }


    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("StateList { \n");
        for (State state: State.values()){
            if (has(state)){
                text.append("   ").append(state.name()).append(" = ").append(get(state)).append("\n");
            } else {
                text.append("   ").append(state.name()).append(" = undefined\n");
            }
        }
        text.append("}");
        return text.toString();
    }

    private enum State {
        FOCUSED,
        PRESSED,
        HOVERED,
        CHECKED,
        CLICKABLE
    }
}