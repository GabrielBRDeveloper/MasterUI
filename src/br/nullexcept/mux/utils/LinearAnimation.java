package br.nullexcept.mux.utils;

import br.nullexcept.mux.app.Looper;

public abstract class LinearAnimation {
    private int duration;
    private boolean loop  = false;
    public LinearAnimation(int duration) {
        this.duration = duration;
    }

    public void play() {
        long[] data  = new long[3];
        data[0] = duration;
        data[2] = System.currentTimeMillis();
        Runnable[] loops = new Runnable[1];

        Runnable update = () -> {
            if (data[1] > data[0]) {
                onEnd();
                if (loop) {
                    play();
                }
                return;
            }
            onFrame((double)data[1]/data[0]);
            data[1] += (System.currentTimeMillis() - data[2]);
            data[2] = System.currentTimeMillis();
            Looper.getMainLooper().post(loops[0]);
        };
        loops[0] = update;
        onBegin();
        Looper.getMainLooper().post(update);
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public abstract void onBegin();
    public abstract void onFrame(double delta);
    public abstract void onEnd();
}
