package br.nullexcept.mux.view.anim;

import br.nullexcept.mux.app.Looper;
import br.nullexcept.mux.view.View;

public abstract class LinearAnimation {
    private int duration;
    private boolean loop  = false;
    private final Looper looper;
    private final Runnable update = this::update;
    private final long[] times = new long[3];
    private boolean playing = false;

    public LinearAnimation(View view, int duration) {
        this.duration = duration;
        this.looper = view.getContext().getMainLooper();
    }

    protected void setDuration(int duration) {
        this.duration = duration;
    }

    private void update() {
        if (times[1] > times[0]) {
            playing = false;
            onEnd();
            if (loop) {
                play();
            }
            return;
        }
        playing = true;
        onFrame((double) times[1]/ times[0]);
        times[1] += (System.currentTimeMillis() - times[2]);
        times[2] = System.currentTimeMillis();
        looper.post(update);
    }

    public void play() {
        looper.cancel(update);
        playing = true;
        times[0] = duration;
        times[1] = 0;
        times[2] = System.currentTimeMillis();

        onBegin();
        looper.post(update);
    }

    public boolean isPlaying() {
        return playing;
    }

    public void stop() {
        if (playing) {
            looper.cancel(update);
            onEnd();
        }
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public abstract void onBegin();
    public abstract void onFrame(double delta);
    public abstract void onEnd();
}
