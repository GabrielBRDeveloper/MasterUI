package br.nullexcept.mux.app.applets;

import br.nullexcept.mux.app.Applet;

public abstract class ClipboardApplet extends Applet {
    public abstract String getContent();
    public abstract void setContent(String content);
}
