package br.nullexcept.mux.graphics.drawable;

import br.nullexcept.mux.graphics.Canvas;
import br.nullexcept.mux.graphics.Drawable;
import br.nullexcept.mux.graphics.DrawableState;
import br.nullexcept.mux.graphics.Rect;

import java.util.ArrayList;

public class LayerListDrawable extends Drawable {
    private final ArrayList<Drawable> layers = new ArrayList<>();

    public void addLayer(Drawable drawable){
        layers.add(drawable);
    }

    @Override
    public void draw(Canvas canvas) {
        for (Drawable drawable: layers){
            drawable.draw(canvas);
        }
    }

    @Override
    public void setBounds(Rect rect) {
        super.setBounds(rect);
        for (Drawable drawable: layers){
            drawable.setBounds(rect);
        }
    }

    @Override
    public boolean setState(DrawableState state) {
        boolean changed = super.setState(state);
        for(Drawable drawable: layers){
            changed |= drawable.setState(state);
        }
        return changed;
    }
}
