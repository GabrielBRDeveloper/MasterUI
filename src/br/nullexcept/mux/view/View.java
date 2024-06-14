package br.nullexcept.mux.view;

import br.nullexcept.mux.C;
import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.app.Looper;
import br.nullexcept.mux.graphics.*;
import br.nullexcept.mux.input.*;
import br.nullexcept.mux.res.AttributeList;
import br.nullexcept.mux.utils.Log;
import br.nullexcept.mux.view.anim.ViewTransition;
import br.nullexcept.mux.view.menu.MenuGroup;
import br.nullexcept.mux.view.menu.MenuItem;

import java.util.Objects;

public class View {
    private static final String LOG_TAG = "View";

    static final int FLAG_REQUIRES_DRAW = 2;
    public static final int VISIBILITY_VISIBLE = 0;
    public static final int VISIBILITY_HIDDEN = 1;
    public static final int VISIBILITY_GONE = 2;

    private final ViewTransition animInfo = new ViewTransition();
    private static int currentHash = 0;
    private ViewGroup parent;
    private final Rect bounds = new Rect();
    private final Rect padding = new Rect();
    private final Rect visibleBounds = new Rect();
    private final StateList state = new StateList();

    private boolean focusable;
    private boolean clickable;
    private boolean hovered;
    private boolean attached;
    private boolean enable;

    private final Point measured = new Point();
    private final int hashCode = hash();
    private final Rect rect = new Rect();
    private Object tag;
    private String id;

    private Drawable background;
    private float alpha = 1.0f;
    private float scale = 1.0f;
    private float rotation = 0.0f;
    private int flags = 0;
    private int visibility = VISIBILITY_VISIBLE;
    private int gravity = Gravity.NO_GRAVITY;
    private ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    private OnClickListener clickListener;
    private PointerIcon pointerIcon = new PointerIcon(PointerIcon.Model.ARROW);
    private final Context context;
    private AttributeList attributes;

    public View(Context context) {
        this(context,null);
    }

    public View(Context context, AttributeList attrs) {
        this.context = context;
        if (attrs == null){
            attrs = context.getResources().obtainStyled("Widget."+getClass().getSimpleName());
        }

        { // SETUP PADDING
            attrs.searchDimension(AttrList.padding, value -> setPadding(value.intValue(), value.intValue(), value.intValue(), value.intValue()));
            attrs.searchDimension(AttrList.paddingLeft, value -> padding.left = value.intValue());
            attrs.searchDimension(AttrList.paddingTop, value -> padding.top = value.intValue());
            attrs.searchDimension(AttrList.paddingRight, value -> padding.right = value.intValue());
            attrs.searchDimension(AttrList.paddingBottom, value -> padding.bottom = value.intValue());
            setPadding(padding.left, padding.top, padding.right, padding.bottom);
        }
        attrs.searchFloat(AttrList.scale, this::setScale);
        attrs.searchFloat(AttrList.rotation, this::setRotation);
        attrs.searchRaw(AttrList.tag, (value) -> setTag(String.valueOf(value)));
        attrs.searchRaw(AttrList.visibility, value -> {
            value = String.valueOf(value).toLowerCase();
            switch (value) {
                case "visible": visibility = VISIBILITY_VISIBLE; break;
                case "hidden": visibility = VISIBILITY_HIDDEN; break;
                case "gone": visibility = VISIBILITY_GONE; break;
                default: throw new IllegalArgumentException("Invalid visibility key: "+value);
            }
        });
        attrs.searchFloat(AttrList.alpha, this::setAlpha);
        attrs.searchDrawable(AttrList.background, this::setBackground);
        attrs.searchRaw(AttrList.pointerIcon, value -> setPointerIcon(new PointerIcon(PointerIcon.Model.fromName(value))));
        attrs.searchRaw(AttrList.id, value -> id = value);
        attrs.searchRaw(AttrList.gravity, value -> setGravity(Gravity.parseGravity(value)));

        state.set(StateList.CLICKABLE, false);

        attributes = attrs;
        Looper.getMainLooper().post(()-> attributes = null);
    }

    protected void showMenu(MenuItem menu, int x, int y) {
        Rect bounds = getBounds();
        x = Math.max(0, Math.min(x, bounds.width()));
        y = Math.max(0, Math.min(y, bounds.height()));
        getParent().showMenu(menu, bounds.left+x, bounds.top+y);
    }

    protected AttributeList initialAttributes() {
        return attributes;
    }

    public final Context getContext() {
        return context;
    }

    public void setBackground(Drawable background) {
        this.background = background;
        invalidate();
    }

    public ViewTransition getTransition() {
        return animInfo;
    }

    public PointerIcon getPointerIcon() {
        return pointerIcon;
    }

    protected View getFocused() {
        if (parent != null) {
            return getParent().getFocused();
        }
        return null;
    }

    public int getVisibility() {
        return visibility;
    }

    public boolean isHovered() {
        return hovered;
    }

    public final void setHovered(boolean hovered) {
        if (hovered != this.hovered){
            this.hovered = hovered;
            onHoveredChanged(hovered);
        }
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
        state.set(StateList.ENABLE, enable);
        changeDrawableState();
    }

    public void setVisibility(int visibility) {
        if (visibility < 0 || visibility > 2) {
            throw new RuntimeException("INVALID VISIBILITY FLAG");
        }
        this.visibility = visibility;
        measure();
        invalidate();
    }

    public boolean isEnable() {
        return enable;
    }

    protected boolean isFocused() {
        return this.equals(getFocused());
    }

    public void onDraw(Canvas canvas) {
        rect.set(0, 0, canvas.getWidth(), canvas.getHeight());
        if (background != null) {
            background.setBounds(rect);
            background.draw(canvas);
        }
    }

    public void onDrawForeground(Canvas canvas) {
    }

    public final int getWidth() {
        return Math.max(1, getVisibleBounds().width());
    }

    public final int getHeight() {
        return Math.max(1, getVisibleBounds().height());
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
        invalidate();
    }

    public void setPointerIcon(PointerIcon pointerIcon) {
        this.pointerIcon = pointerIcon;
    }

    public final void setPadding(int left, int top, int right, int bottom) {
        padding.set(Math.abs(left), Math.abs(top), Math.abs(right),
                Math.abs(bottom));
        measure();
        measureBounds();
        invalidate();
    }

    public final int getPaddingLeft() {
        return padding.left;
    }

    public final int getPaddingTop() {
        return padding.top;
    }

    public final int getPaddingRight() {
        return padding.right;
    }

    public final int getPaddingBottom() {
        return padding.bottom;
    }

    public int getGravity() {
        return gravity;
    }

    public void addFlag(int mask) {
        if ((flags & mask) == 0) {
            flags |= mask;
        }
    }

    public boolean hasFlag(int mask) {
        return (flags & mask) != 0;
    }

    public void subFlag(int mask) {
        if (hasFlag(mask)) {
            flags &= ~mask;
        }
    }

    public ViewGroup getParent() {
        return parent;
    }

    public boolean onCreateContextMenu(MenuGroup menu){
        return false;
    }

    public void setOnClickListener(OnClickListener clickListener) {
        this.clickListener = clickListener;
        clickable = true;
        state.set(StateList.CLICKABLE, true);
        changeDrawableState();
    }

    public void post(Runnable runnable, long time) {
        Looper.getMainLooper().postDelayed(() -> {
            if (isVisible()) {
                runnable.run();
            }
        }, time);
    }

    protected void onHoveredChanged(boolean hovered){
        state.set(StateList.HOVERED, hovered);
        changeDrawableState();
    }

    protected StateList getStateList() {
        return state;
    }

    public final void requestFocus() {
        if (parent != null) {
            parent.requestFocus(this);
        }
    }

    public final void releaseFocus() {
        if (parent != null) {
            parent.requestFocus(null);
            changeDrawableState();
        }
    }

    protected void requestNextFocus() {
        if (parent != null) {
            parent.findNextFocus(this);
        }
    }

    protected void requestBackFocus() {
        if (parent != null) {
            parent.findBackFocus(this);
        }
    }

    public void setFocusable(boolean focusable) {
        if (parent != null && this.focusable && !focusable) {
            parent.requestFocus(null);
        }
        this.focusable = focusable;
        invalidate();
    }

    public void measure() {
        if (parent == null)
            return;

        if (parent.measureChild(this)) {
            ViewGroup.LayoutParams pp = parent.getLayoutParams();
            if (pp.width < 0 || pp.height < 0) {
                parent.measure();
            }
        }
    }

    protected Size onMeasureContent(int parentWidth, int parentHeight) {
        return new Size(0,0);
    }



    public <T extends View> T findViewByTag(Object tag) {
        if (tag != null && Objects.equals(tag, this.tag)) {
            return (T) this;
        }
        return null;
    }

    public <T extends View> T findViewById(String id) {
        if (id != null && Objects.equals(id, this.id)) {
            return (T) this;
        }
        return null;
    }

    protected void dispatchKeyEvent(KeyEvent event) {
        onKeyEvent(event);
    }

    protected boolean dispatchMouseEvent(MouseEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_NONE:
                onMouseMoved(event);
                break;
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_MOVE:
                boolean pressed = (clickable || focusable) && this.onMouseEvent(event);
                state.set(StateList.PRESSED, pressed);
                changeDrawableState();
                if(pressed){
                    event.setTarget(hashCode);
                    return true;
                } else if(event.getTarget() == hashCode){
                    event.setTarget(Event.NONE_TARGET);
                }
                break;
        }
        return false;
    }

    public void onFocusChanged(boolean focused) {
        state.set(StateList.FOCUSED, focused);
        changeDrawableState();
        invalidate();
    }

    protected void onCharEvent(CharEvent charEvent) {
    }

    protected void onKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getAction() == KeyEvent.ACTION_UP && isFocused()) {
            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEY_LEFT:  requestBackFocus(); break;
                case KeyEvent.KEY_RIGHT:  requestNextFocus(); break;
                case KeyEvent.KEY_TAB: {
                    if (keyEvent.hasShift() || keyEvent.hasCtrl()) {
                        requestBackFocus();
                    } else {
                        requestNextFocus();
                    }
                } break;
            }
        }
    }

    protected void onMouseMoved(MotionEvent event) {
    }

    protected boolean onMouseEvent(MouseEvent mouseEvent) {
        changeDrawableState();

        if (clickable && mouseEvent.getAction() == MouseEvent.ACTION_UP &&
                clickListener != null) {
            if (focusable && getParent() != null) {
                getParent().requestFocus(this);
            }
            clickListener.onClick(this);
        }
        return mouseEvent.getAction() == MouseEvent.ACTION_DOWN;
    }

    protected void changeDrawableState() {
        if (background != null){
            if (background.setState(state)){
                invalidate();
            }
        }
    }

    public final void measureBounds() {
        if (parent != null) {
            Point location = parent.getChildLocation(this);
            bounds.set(location.x, location.y, location.x + getMeasuredWidth(),
                    location.y + getMeasuredHeight());
            visibleBounds.set(bounds);
            ViewGroup parent = this.parent;
            while (parent != null) {
                Rect bounds = parent.getBounds();
                visibleBounds.right =
                        Math.min(parent.getMeasuredWidth(), visibleBounds.right);
                visibleBounds.bottom =
                        Math.min(parent.getMeasuredHeight(), visibleBounds.bottom);
                visibleBounds.move(bounds.left, bounds.top);
                parent = parent.getParent();
            }
        }
    }

    protected void onMeasure(int width, int height) {
        measured.set(width, height);
        getParent().requestLayout();
        measure();
        invalidate();
    }

    protected void onAttachedToWindow() {

    }

    protected void onDetachedFromWindow() {

    }

    protected void onViewRootChanged() {
        if (attached && getViewRoot() == null) {
            attached = false;
            onDetachedFromWindow();
        } else if ((!attached) && getViewRoot() != null) {
            attached = true;
            onAttachedToWindow();
        }
        animInfo.reset();
    }

    protected ViewRoot getViewRoot() {
        return parent == null ? null : parent.getViewRoot();
    }

    final void setParent(ViewGroup group) {
        parent = group;
        onViewRootChanged();
    }

    public int getMeasuredWidth() {
        return visibility == VISIBILITY_GONE ? 0 : measured.x;
    }

    public int getMeasuredHeight() {
        return visibility == VISIBILITY_GONE ? 0 : measured.y;
    }

    public ViewGroup.LayoutParams getLayoutParams() {
        return params;
    }

    public final Rect getBounds() {
        return bounds;
    }

    public final Rect getVisibleBounds() {
        return visibleBounds;
    }

    public boolean isFocusable() {
        return focusable;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public final void invalidate() {
        if (hasFlag(FLAG_REQUIRES_DRAW))
            return;

        addFlag(FLAG_REQUIRES_DRAW);
        if (parent != null) {
            parent.invalidate();
        }
    }

    public boolean isVisible() {
        boolean basic =
                visibility == VISIBILITY_VISIBLE &&
                parent != null &&
                parent.isVisible() &&
                bounds.width() > 0 &&
                bounds.height() > 0;

        if (basic) {
            basic &= bounds.left < parent.getMeasuredWidth();
            basic &= bounds.top < parent.getMeasuredHeight();
        }
        return basic;
    }

    public final void setRotation(float rotation) {
        this.rotation = rotation;
        invalidate();
    }

    public final void setScale(float scale) {
        this.scale = Math.max(0, scale);
    }

    public final float getScale() {
        return scale;
    }

    public final float getRotation() {
        return rotation;
    }

    public boolean equals(Object obj) {
        return obj instanceof View && ((View) obj).hashCode == hashCode;
    }

    private static synchronized int hash() {
        currentHash++;
        C.VIEW_COUNT = currentHash;
        return currentHash;
    }

    public int hashCode() {
        return this.hashCode;
    }

    public void setLayoutParams(ViewGroup.LayoutParams params) {
        this.params = params;
        if (parent != null) {
            parent.requestLayout();
        }
        invalidate();
    }

    public void setAlpha(float alpha) {
        this.alpha = Math.max(0.0f, Math.min(1.0f, alpha));
        invalidate();
    }

    public float getAlpha() {
        return alpha;
    }

    public final void dispatchEvent(Event event) {
        if (event instanceof InputEvent){
            switch (((InputEvent) event).getSource()){
                case MOUSE:
                    dispatchMouseEvent((MouseEvent) event);
                    break;
                case KEYBOARD:
                    if (event instanceof CharEvent) {
                        onCharEvent((CharEvent) event);
                    } else {
                        dispatchKeyEvent((KeyEvent) event);
                    }
                    break;
                default:
                    Log.log(LOG_TAG, "unimplemented dispatcher for event type: "+event);
                    break;
            }
        }
    }

    public Object getTag() {
        return tag;
    }

    public interface OnClickListener {
        void onClick(View view);
    }
}