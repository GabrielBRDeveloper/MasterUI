package br.nullexcept.mux.app.applets;

import br.nullexcept.mux.app.Applet;
import br.nullexcept.mux.res.DisplayInformation;

import java.util.List;

public abstract class DisplayApplet extends Applet {
    public abstract DisplayInformation getPrimaryDisplay();
    public abstract List<DisplayInformation> getDisplays();
}