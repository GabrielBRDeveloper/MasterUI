package br.nullexcept.mux.graphics.drawable;

import br.nullexcept.mux.graphics.Canvas;
import br.nullexcept.mux.graphics.Drawable;
import br.nullexcept.mux.graphics.StateList;
import br.nullexcept.mux.graphics.Rect;

import java.util.ArrayList;

public class LayerListDrawable extends Drawable {
    private final Rect tmp = new Rect();
    private final ArrayList<Layer> layers = new ArrayList<>();

    public void addLayer(Drawable drawable){
        addLayer(drawable, new Rect());
    }

    public void addLayer(Drawable drawable, Rect padding){
        layers.add(new Layer(padding, drawable));
    }

    @Override
    public void draw(Canvas canvas) {
        for (Layer layer: layers){
            layer.drawable.draw(canvas);
        }
    }

    @Override
    public void setBounds(Rect rect) {
        super.setBounds(rect);
        for (Layer layer: layers){
            layer.setBounds(rect);
        }
    }

    @Override
    public boolean setState(StateList state) {
        boolean changed = super.setState(state);
        for(Layer layer: layers){
            changed |= layer.drawable.setState(state);
        }
        return changed;
    }

    private final class Layer {
        private final Rect padding;
        private final Drawable drawable;

        private Layer(Rect padding, Drawable drawable) {
            this.padding = padding;
            this.drawable = drawable;
        }

        public void setBounds(Rect rect) {
            tmp.left = rect.left + padding.left;
            tmp.top = rect.top + padding.top;
            tmp.bottom = rect.bottom - padding.bottom;
            tmp.right = rect.right - padding.right;
            drawable.setBounds(tmp);
        }
    }
}
