package br.nullexcept.mux.app;

import br.nullexcept.mux.core.texel.TexelAPI;
import br.nullexcept.mux.res.AttributeList;
import br.nullexcept.mux.view.View;
import br.nullexcept.mux.view.Window;

public class Activity extends Context {
    public static final int FLAG_ACTIVITY_NEW_WINDOW = 0x0000001;

    Window mWindow;
    private View content;
    private boolean running;
    private boolean destroyed;
    private WindowState windowState;
    ActivityStack stack;

    public void onCreate(){
        running = true;
        AttributeList attrs = getResources().obtainStyled("Widget.Window");
        attrs.searchText("title", (text)-> mWindow.setTitle(""+text));
        attrs.searchDrawable("icon", (icon) -> mWindow.setIcon(icon));
        attrs.searchDimension("width", (size) -> mWindow.setSize(size.intValue(), mWindow.getHeight()));
        attrs.searchDimension("height", (size) -> mWindow.setSize(mWindow.getWidth(), size.intValue()));
    }

    public void onDestroy() {
        running = false;
        stack.invalidate();
        destroyed = true;
    }

    public void onPause(){ running = false; }
    public void onResume(){
        running = true;
        if (windowState != null && mWindow != null) {
            windowState.to(mWindow);
            windowState = null;
        }
    }

    protected boolean isRunning() {
        return running;
    }

    public void finish() {
        if (!destroyed) {
            boolean running = this.running;
            onDestroy();
            destroyed = true;

            ActivityStack back = stack.getBackItem();
            Window window = mWindow;
            mWindow = null;
            if (back != null && running) {
                back.getActivity().resumeToWindow(window);
            } else if (window != null){
                window.destroy();
            }
        }
    }

    private void resumeToWindow(Window window) {
        mWindow = window;
        mWindow.setContentView(content);
        onResume();
    }

    public  <T extends Activity> void startActivity(Launch<T> launch) {
        if (!launch.hasFlag(FLAG_ACTIVITY_NEW_WINDOW)) {
            Window window = mWindow;
            onPause();
            windowState = new WindowState(window);
            mWindow = null;

            window.setContentView(null);
            System.gc();
            Activity nw = launch.make();
            nw.stack = stack.newStack(nw);
            nw._args = launch;
            nw.mWindow = window;

            window.setWindowObserver(Application.buildObserver(nw));
            window.getWindowObserver().onCreated();
        } else {
            Activity nw = launch.make();
            nw._args = launch;
            nw.stack = new ActivityStack(nw);
            Application.boot(TexelAPI.createWindow(), nw);
        }
    }

    public <T extends View> T findViewById(String id) {
        return mWindow.getContentView().findViewById(id);
    }

    public void setContentView(String layoutName){
        setContentView(getLayoutInflater().inflate(layoutName));
    }

    public void setContentView(View view){
        content = view;
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

    @Override
    public String toString() {
        return "Activity { "+ getClass().getName() +" }";
    }
}
