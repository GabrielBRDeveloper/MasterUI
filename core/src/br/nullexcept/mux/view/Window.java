package br.nullexcept.mux.view;

import br.nullexcept.mux.graphics.Drawable;
import br.nullexcept.mux.graphics.Size;

public abstract class Window {
    public abstract int getWidth();
    public abstract int getHeight();
    public abstract boolean isVisible();
    public abstract void setTitle(String title);
    public abstract void setResizable(boolean enable);
    public abstract View getContentView();
    public abstract void setContentView(View view);
    public abstract void setSize(int width, int height);
    public abstract void setMinimumSize(int width, int height);
    public abstract void setVisible(boolean visible);
    public abstract void setWindowObserver(WindowObserver observer);
    public abstract WindowObserver getWindowObserver();

    public abstract void reset();
    public abstract void create();
    public abstract void destroy();
    public abstract void setIcon(Drawable icon);

    public abstract CharSequence getTitle();
    public abstract Size getSize();
    public interface WindowObserver {
        void onCreated();
        void onVisibilityChanged(boolean visible);
        void onResize(int width, int height);
        void onDestroy();
    }
}
