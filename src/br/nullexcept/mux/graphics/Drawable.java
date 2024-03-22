package br.nullexcept.mux.graphics;

import br.nullexcept.mux.res.AttributeList;
import br.nullexcept.mux.res.Resources;

public abstract class Drawable {
    private final Rect bounds = new Rect();
    public abstract void draw(Canvas canvas);

    public Rect getBounds() {
        return bounds;
    }

    public void setBounds(Rect rect){
        bounds.set(rect);
    }


    protected void inflate(Resources res, AttributeList attributes) {

    }
}
