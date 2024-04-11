package br.nullexcept.mux.app;

import br.nullexcept.mux.lang.Valuable;
import br.nullexcept.mux.res.AttributeList;
import br.nullexcept.mux.view.AttrList;
import br.nullexcept.mux.view.View;
import br.nullexcept.mux.view.Window;

public class Activity extends Context {
    Window mWindow;
    private boolean running;

    public void onCreate(){
        running = true;
        AttributeList attrs = getResources().obtainStyled("Widget.Window");
        attrs.searchText("title", (text)-> mWindow.setTitle(""+text));
        attrs.searchDrawable("icon", (icon) -> mWindow.setIcon(icon));
    }
    public void onDestroy(){ running = false; }
    public void onPause(){ running = false; }
    public void onResume(){ running = true; }

    protected boolean isRunning() {
        return running;
    }

    public void finish() {
        mWindow.destroy();
        mWindow = null;
    }

    public void switchActivity(Valuable<Activity> provider) {
        Window window = mWindow;
        window.getWindowObserver().onDestroy();
        mWindow.setContentView(null);
        System.gc();
        Activity nw = provider.get();
        nw.mWindow = mWindow;
        mWindow = null;

        window.setWindowObserver(Application.buildObserver(nw));
        window.getWindowObserver().onCreated();
    }

    public <T extends View> T findViewById(String id) {
        return mWindow.getContentView().findViewById(id);
    }

    public void setContentView(String layoutName){
        setContentView(getLayoutInflater().inflate(layoutName));
    }

    public void setContentView(View view){
        mWindow.setContentView(view);
    }

    protected void post(Runnable runnable) {
        postDelayed(runnable, 1);
    }

    protected void postDelayed(Runnable runnable, int time){
        Looper.getMainLooper().postDelayed(()->{
            if(isRunning()) {
                runnable.run();
            }
        }, time);
    }

    protected void setTitle(String title){
        mWindow.setTitle(title);
    }

    public Window getWindow() {
        return mWindow;
    }
}
