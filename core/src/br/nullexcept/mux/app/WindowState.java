package br.nullexcept.mux.app;

import br.nullexcept.mux.view.Window;

class WindowState {
    private final String title;

    public WindowState(Window window) {
        title = window.getTitle()+"";
    }

    public void to(Window window) {
        window.setTitle(title);
    }
}
