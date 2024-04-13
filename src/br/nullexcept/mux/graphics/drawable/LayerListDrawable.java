package br.nullexcept.mux.graphics.drawable;

import br.nullexcept.mux.graphics.Canvas;
import br.nullexcept.mux.graphics.Drawable;
import br.nullexcept.mux.graphics.Rect;
import br.nullexcept.mux.graphics.StateList;

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
    public int getWidth() {
        int width = 1;
        for (Layer layer: layers) {
            width = Math.max(width, layer.drawable.getWidth()+layer.padding.left+layer.padding.right);
        }
        return width;
    }

    @Override
    public int getHeight() {
        int height = 1;
        for (Layer layer: layers) {
            height = Math.max(height, layer.drawable.getWidth()+layer.padding.bottom+layer.padding.top);
        }
        return height;
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
