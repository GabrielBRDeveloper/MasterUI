package br.nullexcept.mux.graphics.drawable;

import br.nullexcept.mux.graphics.*;

import java.util.ArrayList;

public class SelectorDrawable extends Drawable {
    private final ColorDrawable def = new ColorDrawable(Color.TRANSPARENT);
    private final ArrayList<StateDrawable> drawables = new ArrayList<>();
    private Drawable current;


    public void add(Drawable drawable, StateList states){
        drawables.add(new StateDrawable(states, drawable));
    }

    @Override
    public void draw(Canvas canvas) {
        if (current == null){
            current = getCurrent();
        }
        current.draw(canvas);
    }

    private Drawable getCurrent(){
        StateList current = getState();
        for (StateDrawable item: drawables){
            if (!current.hasConflict(item.stateList)){
                return item.drawable;
            }
        }
        return def;
    }

    @Override
    public void setBounds(Rect rect) {
        super.setBounds(rect);
        for (StateDrawable drawable: drawables)
            drawable.drawable.setBounds(rect);
    }

    @Override
    public boolean setState(StateList state) {
        for (StateDrawable item: drawables)
            item.drawable.setState(state);

        Drawable old = current;
        super.setState(state);
        current = getCurrent();

        return !current.equals(old);
    }

    @Override
    public int getWidth() {
        return current == null ? super.getWidth() : current.getWidth();
    }

    @Override
    public int getHeight() {
        return current == null ? super.getHeight() : current.getHeight();
    }

    private class StateDrawable {
        public final StateList stateList;
        public final Drawable drawable;

        private StateDrawable(StateList stateList, Drawable drawable) {
            this.stateList = stateList;
            this.drawable = drawable;
        }
    }
}
