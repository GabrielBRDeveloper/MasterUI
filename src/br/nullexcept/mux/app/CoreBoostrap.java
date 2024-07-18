package br.nullexcept.mux.app;

import br.nullexcept.mux.view.Window;

public interface CoreBoostrap {
    void boot();
    void finish();
    Window makeWindow();
}
