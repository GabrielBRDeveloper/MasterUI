package br.nullexcept.mux.graphics.drawable;

import br.nullexcept.mux.graphics.*;
import br.nullexcept.mux.view.Gravity;

import java.util.ArrayList;

public class LayerListDrawable extends Drawable {
    private final Rect tmp = new Rect();
    private final ArrayList<Layer> layers = new ArrayList<>();

    public void addLayer(Drawable drawable){
        addLayer(drawable, new Rect());
    }

    public void addLayer(Drawable drawable, Rect padding){
        addLayer(drawable, padding, new Size(-1,-1));
    }

    public void addLayer(Drawable drawable, Rect padding, Size size){
        addLayer(drawable, padding, size, Gravity.LEFT);
    }

    public void addLayer(Drawable drawable, Rect padding, Size size, int gravity){
        layers.add(new Layer(padding, size, drawable, gravity));
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
            width = Math.max(width, layer.getWidth()+layer.padding.left+layer.padding.right);
        }
        return width;
    }

    @Override
    public int getHeight() {
        int height = 1;
        for (Layer layer: layers) {
            height = Math.max(height, layer.getHeight()+layer.padding.bottom+layer.padding.top);
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
        private final Size size;
        private final Drawable drawable;
        private final int gravity;

        private Layer(Rect padding, Size size, Drawable drawable, int gravity) {
            this.padding = padding;
            this.size = size;
            this.drawable = drawable;
            this.gravity = gravity;
        }

        public void setBounds(Rect rect) {
            tmp.left = rect.left + padding.left;
            tmp.top = rect.top + padding.top;
            tmp.bottom = rect.bottom - padding.bottom;
            tmp.right = rect.right - padding.right;

            int x = tmp.left;
            int y = tmp.top;

            int vw = tmp.width();
            int vh = tmp.height();

            int cw = vw;
            int ch = vh;

            if (size.width != -1)
                cw = Math.min(cw, size.width);
            if (size.height != -1)
                ch = Math.min(ch, size.height);

            Gravity.applyGravity(gravity,cw,ch,vw,vh, tmp);
            tmp.move(x,y);

            drawable.setBounds(tmp);
        }

        public int getWidth() {
            return size.width == -1 ? drawable.getWidth() : size.width;
        }

        public int getHeight() {
            return size.height == -1 ? drawable.getHeight() : size.height;
        }
    }
}
