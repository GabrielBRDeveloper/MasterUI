package br.nullexcept.mux.view;

public class PointerIcon {
    private final Model model;
    public PointerIcon(Model model){
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    public enum Model {
        ARROW,
        HAND,
        RESIZE,
        TEXT_SELECTION,
        CUSTOM
    }
}
