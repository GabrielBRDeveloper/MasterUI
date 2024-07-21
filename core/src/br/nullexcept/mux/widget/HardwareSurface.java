package br.nullexcept.mux.widget;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.res.AttributeList;
import br.nullexcept.mux.view.View;

@SuppressWarnings("UNSTABLE: That View is not stable and cant work case project change render core.")
public class HardwareSurface extends View {
    public HardwareSurface(Context context) {
        super(context);
    }

    public HardwareSurface(Context context, AttributeList attrs) {
        super(context, attrs);
    }

    public void onRenderBegin(GlFBOSurface render) {}
    public void onRenderFrame() {}
    public void onRenderDestroy() {}

    public interface GlFBOSurface {
        void begin();
        int getFramebuffer();
        int getTexture();
        void resize(int width, int height);
        void end();
    }
}
