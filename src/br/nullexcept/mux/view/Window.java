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
}
