package br.nullexcept.mux.view.anim;

import br.nullexcept.mux.graphics.PointF;

public class ViewTransition {
    private float alpha = 1.0f;
    private final PointF translate = new PointF();
    private final PointF scale = new PointF(1,1);
    private float rotation = 0;

    public ViewTransition() {
        reset();
    }

    public void reset() {
        translate.set(0,0);
        scale.set(1,1);
        rotation = 0;
        alpha = 1.0f;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getAlpha() {
        return alpha;
    }

    public PointF getScale() {
        return scale;
    }

    public PointF getTranslate() {
        return translate;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }
}
