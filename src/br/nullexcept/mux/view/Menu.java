package br.nullexcept.mux.view;

import br.nullexcept.mux.graphics.Drawable;
import br.nullexcept.mux.lang.Function;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Menu {
    private final ArrayList<Group> groups = new ArrayList<>();
    private Function<Item> clickListener;

    public void setOnClickListener(Function<Item> click) {
        clickListener = click;
    }

    public void callOnClick(Item item) {
        if (clickListener != null)
            clickListener.call(item);
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void add(Group item) {
        groups.add(item);
    }

    public static class Group {
        private final String title;
        private final ArrayList<Item> items = new ArrayList<>();

        public Group(String title, List<Item> items) {
            this.title = title;
            this.items.addAll(items);
        }

        public Group(String title) {
            this(title, Collections.emptyList());
        }

        public void add(Item item) {
            items.add(item);
        }

        public List<Item> getItems() {
            return items;
        }

        public String getTitle() {
            return title;
        }
    }

    public static class Item {
        private final String id;
        private final String title;
        private final Drawable icon;

        public Item(String id, String title, Drawable icon) {
            this.id = id;
            this.title = title;
            this.icon = icon;
        }

        public String getTitle() {
            return title;
        }

        public String getId() {
            return id;
        }

        public Drawable getIcon() {
            return icon;
        }
    }
}
