package br.nullexcept.mux.app;

public class Service {
    Looper myLooper;

    protected void onCreate() {

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
