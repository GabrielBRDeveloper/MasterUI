package br.nullexcept.mux.view;

import br.nullexcept.mux.graphics.Rect;

public class MarginLayoutParams extends ViewGroup.LayoutParams {
    private final Rect margins = new Rect();

    public MarginLayoutParams(int width, int height) {
        super(width, height);
    }

    @Override
    public void from(ViewGroup.LayoutParams params) {
        if (params instanceof MarginLayoutParams) {
            margins.set(((MarginLayoutParams) params).margins);
        }
        super.from(params);
    }

    public void setPosition(int left, int top) {
        setMargins(left, top, 0,0);
    }

    public void setMargins(int left, int top, int right, int bottom) {
        margins.set(left, top, right, bottom);
    }

    public int getMarginLeft() {
        return margins.left;
    }

    public Rect getMargins() {
        return margins;
    }

    public int getMarginTop() {
        return margins.top;
    }

    public int getMarginBottom() {
        return margins.bottom;
    }

    public int getMarginRight() {
        return margins.right;
    }
}
