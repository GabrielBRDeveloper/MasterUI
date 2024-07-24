package br.nullexcept.mux.app.base;

public interface Lifecycle {
    void onCreate();
    void onPause();
    void onResume();
    void onDestroy();
}
