package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.view.View;
import br.nullexcept.mux.widget.HardwareSurface;

class RenderCache {
    public CanvasTexel canvas;
    public View view;
    public boolean valid;
    private boolean disposed = false;

    public RenderCache(View view) {
        this.view = view;
    }

    public RenderCache createCanvas(){
        canvas = new CanvasTexel(16,16);
        if (view instanceof HardwareSurface) {
            ((HardwareSurface) view).onRenderBegin(new HardwareSurface.GlFBOSurface() {
                @Override
                public void begin() {
                    canvas.begin();
                }

                @Override
                public int getFramebuffer() {
                    return canvas.getFramebuffer().getFramebuffer();
                }

                @Override
                public int getTexture() {
                    return canvas.getFramebuffer().getTexture().getTexture();
                }

                @Override
                public void resize(int width, int height) {
                    canvas.getFramebuffer().resize(width, height);
                }

                @Override
                public void end() {
                    canvas.end();
                }
            });
        }
        return this;
    }

    public void dispose() {
        if (disposed) {
            return;
        }
        disposed = true;
        canvas.dispose();
        if (view instanceof HardwareSurface) {
            ((HardwareSurface) view).onRenderDestroy();
        }
    }
}
