package br.nullexcept.mux.view.anim;

import br.nullexcept.mux.view.View;

public class RotationAnimation extends LinearAnimation {
    private final View view;
    private float from;
    private float to;
    private float old;

    public RotationAnimation(View vw, int duration) {
        super(vw, duration);
        view = vw;
        from = 0;
        to = 360;
    }

    public void setRotation(float from, float to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public void onBegin() {
        old = view.getRotation();
        view.setRotation(from);
    }

    @Override
    public void onFrame(double delta) {
        view.setRotation((float) (from + ((to-from)*delta)));
    }

    @Override
    public void onEnd() {
        view.setRotation(old);
    }
}
