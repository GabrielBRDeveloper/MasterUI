package br.nullexcept.mux.app;

import br.nullexcept.mux.lang.Function;
import br.nullexcept.mux.lang.Valuable;
import br.nullexcept.mux.view.View;
import br.nullexcept.mux.view.Window;

public class Activity extends Context {
    Window mWindow;

    public void onCreate(){}
    public void onDestroy(){}
    public void onPause(){}
    public void onResume(){}

    public void finish() {
        mWindow.destroy();
        mWindow = null;
    }

    public void switchActivity(Valuable<Activity> provider) {
        Window window = mWindow;
        finish();
        Application.boot(window, provider.get());
    }

    public void setContentView(String layoutName){
        setContentView(getLayoutInflater().inflate(layoutName));
    }

    public void setContentView(View view){
        mWindow.setContentView(view);
    }

    protected void post(Runnable runnable){
        Looper.getMainLooper().post(runnable);
    }

    protected void setTitle(String title){
        mWindow.setTitle(title);
    }

    public Window getWindow() {
        return mWindow;
    }
}
