package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.graphics.Color;
import br.nullexcept.mux.graphics.Rect;
import br.nullexcept.mux.graphics.drawable.ColorDrawable;
import br.nullexcept.mux.input.*;
import br.nullexcept.mux.res.LayoutInflater;
import br.nullexcept.mux.view.Menu;
import br.nullexcept.mux.view.View;
import br.nullexcept.mux.view.ViewGroup;
import br.nullexcept.mux.view.Window;
import br.nullexcept.mux.view.anim.AlphaAnimation;
import br.nullexcept.mux.widget.AbsoluteLayout;
import br.nullexcept.mux.widget.ImageView;
import br.nullexcept.mux.widget.LinearLayout;
import br.nullexcept.mux.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class WindowContainer extends AbsoluteLayout {
    static final int FLAG_REQUIRES_DRAW = 2;
    private static final int FLAG_FOCUSED = 4;

    private final HashMap<Integer, RenderCache> renders = new HashMap<>();
    private final CanvasTexel rootCanvas;
    private final ViewRenderer drawer;
    private Menu currentMenu;

    private final LinearLayout menuLayout;
    private final AbsoluteLayout content;

    private int focusedView = hashCode();

    public WindowContainer(Context context, Window window) {
        super(context);
        setBackground(new ColorDrawable(Color.BLACK));
        rootCanvas = new CanvasTexel(64,64);
        getBounds().set(0,0,64,64);
        drawer = new ViewRenderer(renders);
        content = new AbsoluteLayout(context);
        content.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        super.addChild(content);
        menuLayout = new LinearLayout(context, context.getResources().obtainStyled("Widget.MenuLayout"));
        super.addChild(menuLayout);

    }

    @Override
    public void measure() {
        if (rootCanvas != null) {
            getBounds().set(0, 0, rootCanvas.getWidth(), rootCanvas.getHeight());
        }
        while (measureChildren()) {
            measure();
        }
    }

    @Override
    public int getMeasuredHeight() {
        return rootCanvas.getHeight();
    }

    @Override
    public int getMeasuredWidth() {
        return rootCanvas.getWidth();
    }

    @Override
    protected void onTreeChanged() {
        super.onTreeChanged();
        mountRenderBag();
        requestFocus(this);
        requestLayout();
    }

    @Override
    public void removeAllViews() {
        content.removeAllViews();
    }

    @Override
    public void removeChild(View view) {
        content.removeChild(view);
    }

    @Override
    protected void onMeasure(int width, int height) {
        super.onMeasure(rootCanvas.getWidth(), rootCanvas.getHeight());
    }

    private void mountRenderBag(ViewGroup group){
        for (View child: group.getChildren()){
            int hash = child.hashCode();
            if (!renders.containsKey(hash)){
                RenderCache render = new RenderCache(child);
                render.canvas = new CanvasTexel(1,1);
                renders.put(hash, render);
            }
            renders.get(hash).valid = true;
            if (child instanceof ViewGroup){
                mountRenderBag((ViewGroup) child);
            }
        }
    }

    @Override
    protected void requestFocus(View focused) {
        focused = focused == null ? this : focused;
        if (!focused.hasFlag(FLAG_FOCUSED)){
            if (renders.containsKey(focusedView)){
                View oldFocus = renders.get(focusedView).view;
                oldFocus.onFocusChanged(false);
                oldFocus.subFlag(FLAG_FOCUSED);
            }
            focusedView = focused.hashCode();
            focused.onFocusChanged(true);
            focused.addFlag(FLAG_FOCUSED);
        }
    }

    void invalidateAll(){
        for (RenderCache render: renders.values()){
            render.view.addFlag(FLAG_REQUIRES_DRAW);
        }
        addFlag(FLAG_REQUIRES_DRAW);
    }

    private void mountRenderBag(){
        for (RenderCache render: renders.values())
            render.valid = false;

        mountRenderBag(this);
        ArrayList<RenderCache> renders = new ArrayList<>(this.renders.values());
        for (RenderCache render: renders){
            if (!render.valid){
                this.renders.remove(render.view.hashCode());
                render.canvas.dispose();
            }
        }
        invalidateAll();
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    private long lastRefresh = 0;
    public void drawFrame() {
        if (System.currentTimeMillis() - lastRefresh >= 7000){
            invalidateAll();
            lastRefresh = System.currentTimeMillis();
        }
        drawer.drawInternal(rootCanvas, this);
    }

    @Override
    public void addChild(View view) {
        //For allow only 1 view
        content.removeAllViews();
        view.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        content.addChild(view);
    }

    @Override
    public void requestLayout() {
        measure();
    }

    public void dispose(){
        for (RenderCache render: renders.values()){
            render.canvas.dispose();
        }
        renders.clear();
    }

    public void performInputEvent(Event event) {
        if (event instanceof MouseEvent){
            if(performMenuEvent((MouseEvent) event)) {
                return;
            }
            performHover((MouseEvent)event);
            if(event.getTarget() == -1){
                dispatchEvent(event);
            } else if (renders.containsKey(event.getTarget())){
                View target = renders.get(event.getTarget()).view;
                Rect visibleBounds = target.getVisibleBounds();
                ((MouseEvent) event).transform(-visibleBounds.left, -visibleBounds.top);
                target.dispatchEvent(event);
                ((MouseEvent) event).transform(visibleBounds.left, visibleBounds.top);
            }
        } else if ((event instanceof KeyEvent)||(event instanceof CharEvent)){
            if (focusedView != hashCode()){
                View focused = renders.get(focusedView).view;
                focused.dispatchEvent(event);
            }
        } else {
            dispatchEvent(event);
        }
    }

    private void closeMenu() {
        menuLayout.removeAllViews();
        menuLayout.measure();
    }

    private boolean performMenuEvent(MouseEvent event) {
        if (event.getButton() == MouseEvent.BUTTON_NONE)
            return false;

        if (menuLayout.getMeasuredWidth() == 0) {
            if(event.getButton() == MouseEvent.RIGHT_BUTTON  && event.getAction() == MotionEvent.ACTION_UP) {
                View child = content.getChildAt((int)event.getX(), (int)event.getY());
                currentMenu = new Menu();
                while (child != null && !(child.onCreateContextMenu(currentMenu))) {
                    child = child.getParent();
                }
                if (child != null) {
                    AbsoluteLayout.LayoutParams params = (LayoutParams) menuLayout.getLayoutParams();
                    params.x = Math.round(event.getX());
                    params.y = Math.round(event.getY());
                    showMenu();
                    return true;
                }

            }
            return false;
        }
        Rect bounds = menuLayout.getBounds();
        if (bounds.inner(event.getX(), event.getY())) {
            return false;
        } else {
            closeMenu();
        }
        return true;
    }

    private void showMenu() {
        LayoutInflater inflater  = getContext().getLayoutInflater();
        for (Menu.Group group: currentMenu.getGroups()) {
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
                v.setOnClickListener((x)-> {
                    currentMenu.callOnClick(item);
                    closeMenu();
                });
                menuLayout.addChild(v);
                new AlphaAnimation(v, 250).play();
            }
        }

        menuLayout.measure();
        requestLayout();
    }

    private void populateHover(View view, boolean hovered) {
        while (view != null) {
            view.setHovered(hovered);
            view = view.getParent();
        }
    }

    private View lastHover;
    private void performHover(MouseEvent event) {
        View view = getChildAt((int)event.getX(), (int)event.getY());

        if (view != null) {
            if (view.equals(lastHover)) {
                return;
            }
            if (lastHover != null)
                populateHover(lastHover, false);

            populateHover(view, true);
        }
        if (view == null && lastHover != null){
            populateHover(lastHover, false);
        }

        lastHover = view;
    }

    public CanvasTexel getCanvas() {
        return rootCanvas;
    }

    public void resize(int w, int h) {
        rootCanvas.getFramebuffer().resize(w, h);
        measure();
        requestLayout();
        invalidateAll();
    }

    @Override
    protected View getFocused() {
        if (renders.containsKey(focusedView)){
            return renders.get(focusedView).view;
        }
        return null;
    }
}
