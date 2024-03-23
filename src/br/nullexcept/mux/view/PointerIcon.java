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
        CUSTOM;

        public static Model fromName(String value) {
            switch (value){
                case "hand":
                    return HAND;
                case "resize":
                    return RESIZE;
                case "textSelection":
                    return TEXT_SELECTION;
            }
            return ARROW;
        }
    }
}
