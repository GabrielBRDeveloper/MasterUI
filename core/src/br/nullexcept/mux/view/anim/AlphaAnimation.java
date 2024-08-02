package br.nullexcept.mux.view.anim;

import br.nullexcept.mux.view.View;

public class AlphaAnimation extends LinearAnimation {
    private final View view;
    private float src, from, to, diff;

    public AlphaAnimation(View view, int duration) {
        super(view, duration);
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
        src = view.getTransition().getAlpha();
    }

    @Override
    public void onFrame(double delta) {
        view.getTransition().setAlpha(from+(float) (diff * delta));
        view.invalidate();
    }

    @Override
    public void onEnd() {
        view.setAlpha(src);
    }
}
