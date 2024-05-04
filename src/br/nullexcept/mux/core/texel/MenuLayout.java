package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.res.LayoutInflater;
import br.nullexcept.mux.view.ViewGroup;
import br.nullexcept.mux.view.anim.AlphaAnimation;
import br.nullexcept.mux.view.menu.MenuGroup;
import br.nullexcept.mux.view.menu.MenuItem;
import br.nullexcept.mux.widget.AbsoluteLayout;
import br.nullexcept.mux.widget.ImageView;
import br.nullexcept.mux.widget.LinearLayout;
import br.nullexcept.mux.widget.TextView;

class MenuLayout extends LinearLayout {
    public MenuLayout(Context context) {
        super(context, context.getResources().obtainStyled("Widget.MenuLayout"));
    }

    public void show(MenuItem menu) {
        LayoutInflater inflater  = getContext().getLayoutInflater();
        inflate(inflater, menu, menu.isEnable());

        new AlphaAnimation(this, 100).play();
        measure();
        requestLayout();
    }

    private void inflate(LayoutInflater inflater, MenuItem item, boolean enable) {
        if (item instanceof MenuGroup) {
            for (MenuItem i : ((MenuGroup) item).getChildren()) {
                inflate(inflater, i, enable && item.isEnable());
            }
            return;
        }

        ViewGroup v = inflater.inflate("default_widget_menu_item");
        v.setAlpha(enable && item.isEnable() ? 1.0f : 0.5f );
        ImageView icon = v.findViewByTag("icon");
        if (item.getIcon() == null) {
            icon.getParent().removeChild(icon);
        } else {
            icon.setImageDrawable(item.getIcon());
        }
        ((TextView)v.findViewByTag("title")).setText(item.getTitle());
        if (item.isEnable()) {
            v.setOnClickListener((x) -> {
                item.callOnClick();
                hide();
            });
        }
        addChild(v);
    }

    public void setPosition(int x, int y) {
        AbsoluteLayout.LayoutParams params = (AbsoluteLayout.LayoutParams) getLayoutParams();
        params.x = x;
        params.y = y;
        measure();
    }

    public void hide() {
        removeAllViews();
        measure();
    }
}
