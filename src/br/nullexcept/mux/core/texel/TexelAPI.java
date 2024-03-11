package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.app.Activity;
import br.nullexcept.mux.view.Window;

public class TexelAPI {
    public static void initialize() {
        VgTexel.initialize();
    }

    public static void destroy() {
        VgTexel.destroy();
    }

    public static Window createWindow(Activity context) {
        return new GlfwWindow(context);
    }
}
