package br.nullexcept.mux.view.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MenuGroup extends MenuItem {
    private final ArrayList<MenuItem> items = new ArrayList<>();

    public MenuGroup(String id, List<MenuItem> items) {
        super(id, "","",null);
        this.items.addAll(items);
    }

    public MenuGroup(String id) {
        this(id, Collections.emptyList());
    }

    public MenuGroup() {
        this("");
    }

    public void add(MenuItem item) {
        items.add(item);
    }

    public void remove(MenuItem item) {
        items.remove(item);
    }

    public void clear() {
        items.clear();
    }

    public int getChildrenCount() {
        return items.size();
    }

    public List<MenuItem> getChildren() {
        return new ArrayList<>(items);
    }

    public <T extends MenuItem> T getChild(int index) {
        return (T) items.get(index);
    }

    @Override
    public <T extends MenuItem> T findMenuById(String id) {
        for (MenuItem item: items) {
            MenuItem i = item.findMenuById(id);
            if (i != null) {
                return (T) i;
            }
        }
        return super.findMenuById(id);
    }
}
