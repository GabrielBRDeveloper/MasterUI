package br.nullexcept.mux.app;

import br.nullexcept.mux.view.Window;

import java.util.Map;

public interface CoreBoostrap {
    void boot();
    void finish();
    Window makeWindow();
    Map<? extends String,? extends Applet> getSystemApplets();
}
