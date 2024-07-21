package br.nullexcept.mux.app;

public class Service extends Context {
    Looper myLooper;

    protected void onCreate() {

    }

    protected void post(Runnable runnable) {
        postDelayed(runnable, 1);
    }

    protected void postDelayed(Runnable run, int time) {
        myLooper.postDelayed(run, time);
    }

    protected void finish() {
        myLooper.stop();
    }

    public Looper getLooper() {
        return myLooper;
    }

    protected void onDestroy() {

    }
}
