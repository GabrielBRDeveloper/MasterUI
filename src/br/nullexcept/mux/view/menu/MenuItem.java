package br.nullexcept.mux.view.menu;

import br.nullexcept.mux.graphics.Drawable;

public class MenuItem {
    private final String title;
    private final String summary;
    private final Drawable icon;
    private final String id;
    private boolean enable = true;
    private OnMenuClickListener clickListener;

    public MenuItem(String id, String title, Drawable icon) {
        this(id, title, "", icon);
    }

    public MenuItem(String id, String title, String summary, Drawable icon) {
        this.title = title;
        this.summary = summary;
        this.icon = icon;
        this.id = id;
    }

    public void setOnClickListener(OnMenuClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void callOnClick() {
        if (clickListener != null) {
            clickListener.onClick(this);
        }
    }

    public String getTitle() {
        return title;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getId() {
        return id;
    }

    public String getSummary() {
        return summary;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public <T extends MenuItem> T findMenuById(String id) {
        return String.valueOf(this.id).equals(id) ? (T) this : null;
    }

    public interface OnMenuClickListener {
        void onClick(MenuItem item);
    }
}
