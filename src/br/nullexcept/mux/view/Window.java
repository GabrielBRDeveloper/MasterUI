package br.nullexcept.mux.view;

public abstract class Window {
    public abstract int getWidth();
    public abstract int getHeight();
    public abstract boolean isVisible();
    public abstract void setTitle(String title);
    public abstract void setResizable(boolean enable);
    public abstract View getContentView();
    public abstract void setContentView(View view);
    public abstract void setVisible(boolean visible);
    public abstract void setWindowObserver(WindowObserver observer);
    public abstract void create();
    public abstract void destroy();

    public interface WindowObserver {
        void onCreated();
        void onVisibilityChanged(boolean visible);
        void onResize(int width, int height);
        void onDestroy();
    }
}
