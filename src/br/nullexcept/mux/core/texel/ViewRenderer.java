package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.graphics.Rect;
import br.nullexcept.mux.lang.Log;
import br.nullexcept.mux.view.View;
import br.nullexcept.mux.view.ViewGroup;

import java.util.HashMap;
import java.util.List;

class ViewRenderer {
    private static final int FLAG_REQUIRES_DRAW = WindowContainer.FLAG_REQUIRES_DRAW;
    private final HashMap<Integer, RenderCache> registry;

    ViewRenderer(HashMap<Integer, RenderCache> registry){
        this.registry = registry;
    }

    public void drawInternal(CanvasTexel canvas, View view){

        if (!view.hasFlag(FLAG_REQUIRES_DRAW)) {
            return;
        }

        Rect bounds = view.getBounds();
        if (bounds.width() != canvas.getWidth() || bounds.height() != canvas.getHeight()) {
            canvas.getFramebuffer().resize(bounds.width(), bounds.height());
        }
        canvas.begin();
        canvas.reset();
        canvas.alpha(view.getAlpha());
        view.onDraw(canvas);
        if (view instanceof ViewGroup){
            canvas.end();
            List<View> children = ((ViewGroup) view).getChildren();
            int drawables = 0;
            for (View child: children){
                if (child.isVisible()) {
                    this.draw(child);
                    drawables++;
                }
            }
            float[][] borders = new float[drawables][2*4];
            float[] alphas = new float[drawables];
            int[] textures = new int[drawables];
            int index = 0;
            for (View child: children){
                if (child.isVisible()){
                    CanvasTexel texel = registry.get(child.hashCode()).canvas;
                    borders[index] = createMesh(canvas, child);
                    alphas[index] = child.getAlpha();
                    textures[index] = texel.getFramebuffer().getTexture().getTexture();
                    index++;
                }
            }
            canvas.getFramebuffer().bind();
            GLTexel.drawViewLayers(borders,textures, alphas);
            canvas.getFramebuffer().unbind();
            canvas.begin();
        }
        view.onDrawForeground(canvas);
        view.subFlag(FLAG_REQUIRES_DRAW);
        canvas.end();
    }

    public void draw(View view) {
        if (!view.isVisible()){
            return;
        }
        RenderCache item = registry.get(view.hashCode());
        drawInternal(item.canvas, view);
    }

    public float[] createMesh(CanvasTexel dest, View view){
        float w = dest.getWidth();
        float h = dest.getHeight();
        Rect bounds = view.getBounds();
        float vw = bounds.width();
        float vh = bounds.height();
        float x = bounds.left;
        float y = bounds.top;


        if (view.getScale() != 1.0){
            final float nw = vw * view.getScale();
            final float nh = vh * view.getScale();

            x += (vw - nw)/2;
            y += (vh - nh)/2;

            vw = nw;
            vh = nh;
        }

        // CONVERT GL BOTTOM LEFT TO TOP LEFT
        y = h - y - vh;

        float[] mesh = new float[]{
                x   ,   y,
                x+vw,   y,
                x   ,   y+vh,
                x+vw,   y+vh
        };


        /**
         *    @TODO: NEED MAKE
         *  - Transform by viewport.
         *  - Scale by view.
         *  - Rotate by view
         */

        if (view.getRotation() != 0){
            // PI/180 = Degrees to radians
            final double radians = (view.getRotation() * (Math.PI/180.0));
            // Configure pivots
            final float px = x + (vw/2.0f);
            final float py = y + (vh/2.0f);

            final double cos = Math.cos(radians);
            final double sin = Math.sin(radians);

            for (int i = 0; i < 4*2; i+=2){
                mesh[i] -= px;
                mesh[i+1] -= py;

                float ix = (float) (mesh[i] * cos - mesh[i+1] * sin);
                float iy = (float) (mesh[i] * sin + mesh[i+1] * cos);

                mesh[i] = (ix + px);
                mesh[i+1] = (iy + py);
            }
        }

        float wa = 2.0f/w;
        float ha = 2.0f/h;

        for (int i = 0; i < 4*2; i+=2){
            mesh[i] *= wa;
            mesh[i+1] *= ha;

            mesh[i] -= 1.0;
            mesh[i+1] -= 1.0;
        }

        return mesh;
    }

}
