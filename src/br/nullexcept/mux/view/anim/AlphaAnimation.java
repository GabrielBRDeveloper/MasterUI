package br.nullexcept.mux.view.anim;

import br.nullexcept.mux.utils.LinearAnimation;
import br.nullexcept.mux.view.View;

public class AlphaAnimation extends LinearAnimation {
    private final View view;
    private float src, from, to, diff;

    public AlphaAnimation(View view, int duration) {
        super(duration);
        this.view = view;
        setAlpha(0.0f, 1.0f);
    }

    public void setAlpha(float from, float dest) {
        this.from = from;
        this.to = dest;
        this.diff = to - from;
    }

    @Override
    public void onBegin() {
        src = view.getAlpha();
        view.setAlpha(from);
    }

    @Override
    public void onFrame(double delta) {
        view.setAlpha(from+(float) (diff * delta));
    }

    @Override
    public void onEnd() {
        view.setAlpha(src);
    }
}
