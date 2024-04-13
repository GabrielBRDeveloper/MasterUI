package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.res.LayoutInflater;
import br.nullexcept.mux.view.Menu;
import br.nullexcept.mux.view.ViewGroup;
import br.nullexcept.mux.view.anim.AlphaAnimation;
import br.nullexcept.mux.widget.AbsoluteLayout;
import br.nullexcept.mux.widget.ImageView;
import br.nullexcept.mux.widget.LinearLayout;
import br.nullexcept.mux.widget.TextView;

class MenuLayout extends LinearLayout {
    public MenuLayout(Context context) {
        super(context, context.getResources().obtainStyled("Widget.MenuLayout"));
    }

    public void show(Menu menu) {
        LayoutInflater inflater  = getContext().getLayoutInflater();
        for (Menu.Group group: menu.getGroups()) {
            for (Menu.Item item: group.getItems()) {
                ViewGroup v = inflater.inflate("default_widget_menu_item");
                v.setAlpha(item.isEnable() ? 1.0f : 0.5f );
                ImageView icon = v.findViewByTag("icon");
                if (item.getIcon() == null) {
                    icon.getParent().removeChild(icon);
                } else {
                    icon.setImageDrawable(item.getIcon());
                }
                ((TextView)v.findViewByTag("title")).setText(item.getTitle());
                if (item.isEnable()) {
                    v.setOnClickListener((x) -> {
                        menu.callOnClick(item);
                        hide();
                    });
                }
                addChild(v);
            }
        }

        new AlphaAnimation(this, 100).play();
        measure();
        requestLayout();
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
