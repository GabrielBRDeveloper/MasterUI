package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.view.View;

class RenderCache {
    public CanvasTexel canvas;
    public View view;
    public boolean valid;

    public RenderCache(View view) {
        this.view = view;
    }
}
