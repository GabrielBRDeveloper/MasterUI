package br.nullexcept.mux.view;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.app.Looper;
import br.nullexcept.mux.graphics.Canvas;
import br.nullexcept.mux.graphics.Drawable;
import br.nullexcept.mux.graphics.Point;
import br.nullexcept.mux.graphics.Rect;
import br.nullexcept.mux.hardware.CharEvent;
import br.nullexcept.mux.input.Event;
import br.nullexcept.mux.input.KeyEvent;
import br.nullexcept.mux.input.MouseEvent;
import br.nullexcept.mux.lang.Function;
import br.nullexcept.mux.lang.ValuedFunction;
import br.nullexcept.mux.res.AttributeList;
import java.util.Objects;

public class View {
  static final int FLAG_REQUIRES_DRAW = 2;

  private static int currentHash = 0;
  private ViewGroup parent;
  private final Rect bounds = new Rect();
  private final Rect padding = new Rect();
  private final Rect visibleBounds = new Rect();
  private boolean focusable;
  private boolean clickable;
  private final Point measured = new Point();
  private final int hashCode = hash();
  private final Rect rect = new Rect();
  private Object tag;
  private Drawable background;
  private float alpha = 1.0f;
  private int flags = 0;
  private int gravity = Gravity.NO_GRAVITY;
  private ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
  private OnClickListener clickListener;
  private final Context context;

  public View(Context context) {
    this(context, null);
  }

  public View(Context context, AttributeList attrs) {
    this.context = context;
    onRequestAttribute(attrs);
  }

  protected void onRequestAttribute(AttributeList attr) {
    {// SETUP LAYOUT PARAMS

      ViewGroup.LayoutParams params = getLayoutParams();
      attr.searchRaw(ViewAttrs.width, value -> {
        if ("match_parent".equals(value)){
          params.width= ViewGroup.LayoutParams.MATCH_PARENT;
        } else if ("wrap_content".equals(value)){
          params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
          params.width = (int) attr.getDimension(ViewAttrs.width, 0.0f);
        }
      });

      attr.searchRaw(ViewAttrs.height, value -> {
        if ("match_parent".equals(value)){
          params.height= ViewGroup.LayoutParams.MATCH_PARENT;
        } else if ("wrap_content".equals(value)){
          params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
          params.height = (int) attr.getDimension(ViewAttrs.height, 0.0f);
        }
      });

      setLayoutParams(params);
    }
    { // SETUP PADDING
      attr.searchDrawable(ViewAttrs.background, this::setBackground);
      attr.searchDimension(ViewAttrs.padding, value -> setPadding(value.intValue(), value.intValue(), value.intValue(), value.intValue()));
      attr.searchDimension(ViewAttrs.paddingLeft, value -> padding.left = value.intValue());
      attr.searchDimension(ViewAttrs.paddingTop, value -> padding.top = value.intValue());
      attr.searchDimension(ViewAttrs.paddingRight, value -> padding.right = value.intValue());
      attr.searchDimension(ViewAttrs.paddingBottom, value -> padding.bottom = value.intValue());
      setPadding(padding.left, padding.top, padding.right, padding.bottom);
    }
    attr.searchText(ViewAttrs.tag, (value)-> setTag(String.valueOf(tag)));
    attr.searchFloat(ViewAttrs.alpha, this::setAlpha);
  }

  public final Context getContext() {
    return context;
  }

  public void setBackground(Drawable background) {
    this.background = background;
    invalidate();
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

  public void setOnClickListener(OnClickListener clickListener) {
    this.clickListener = clickListener;
    clickable = true;
  }

  public void post(Runnable runnable, long time) {
    Looper.getMainLooper().postDelayed(() -> {
      if (isVisible()) {
        runnable.run();
      }
    }, time);
  }

  public final void requestFocus() {
    if (parent != null) {
      parent.requestFocus(this);
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

    int parentWidth = parent.getMeasuredWidth() - parent.getPaddingLeft() -
            parent.getPaddingRight();
    int parentHeight = parent.getMeasuredHeight() - parent.getPaddingTop() -
            parent.getPaddingBottom();

    Point location = parent.getChildLocation(this);
    int ow = getMeasuredWidth();
    int oh = getMeasuredHeight();
    { // Measure sizes
      int w = 1;
      int h = 1;
      if (params.width >= 0) {
        w = params.width;
      } else if (params.height == ViewGroup.LayoutParams.MATCH_PARENT) {
        w = Math.max(0, parentWidth - location.x);
      } else {
        w = calculateWidth();
      }

      if (params.height >= 0) {
        h = params.height;
      } else if (params.height == ViewGroup.LayoutParams.MATCH_PARENT) {
        h = Math.max(0, parentHeight - location.y);
      } else {
        h = calculateHeight();
      }
      if (ow != w || oh != h) {
        onMeasure(w, h);
        parent.requestLayout();
        measure();
        invalidate();
      }
    }
  }

  protected int calculateWidth() {
    return padding.left + padding.right;
  }

  protected int calculateHeight() {
    return padding.top + padding.bottom;
  }

  public <T extends View> T findViewByTag(Object tag) {
    if (tag != null && Objects.equals(tag, this.tag)) {
      return (T) this;
    }
    return null;
  }

  public void onFocusChanged(boolean focused) {
    invalidate();
  }

  protected void dispatchKeyEvent(KeyEvent event) {
    onKeyEvent(event);
  }

  public boolean dispatchMouseEvent(MouseEvent mouseEvent) {
    if ((focusable || clickable) && this.onMouseEvent(mouseEvent)) {
      mouseEvent.setTarget(hashCode);
      return true;
    }
    if (mouseEvent.getTarget() == hashCode) {
      mouseEvent.setTarget(-1);
    }
    return false;
  }

  protected void onCharEvent(CharEvent charEvent) {
  }

  protected void onKeyEvent(KeyEvent keyEvent) {
  }

  protected boolean onMouseEvent(MouseEvent mouseEvent) {
    if (clickable && mouseEvent.getAction() == MouseEvent.ACTION_UP &&
            clickListener != null) {
      if (focusable && getParent() != null) {
        getParent().requestFocus(this);
      }
      clickListener.onClick(this);
    }
    return mouseEvent.getAction() == MouseEvent.ACTION_DOWN;
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
  }

  final void setParent(ViewGroup group) {
    parent = group;
  }

  public int getMeasuredWidth() {
    return measured.x;
  }

  protected void getInternalSize(Rect dest) {
    dest.left = getPaddingLeft();
    dest.top = getPaddingTop();
    dest.right = getMeasuredWidth() - getPaddingRight();
    dest.bottom = getMeasuredHeight() - getPaddingBottom();
  }

  public int getMeasuredHeight() {
    return measured.y;
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
    boolean basic = parent != null && parent.isVisible() &&
            bounds.width() > 0 && bounds.height() > 0;
    if (!basic) {
      basic &= bounds.left < parent.getMeasuredWidth();
      basic &= bounds.top < parent.getMeasuredHeight();
    }
    return basic;
  }

  public boolean equals(Object obj) {
    return obj instanceof View && ((View) obj).hashCode == hashCode;
  }

  private static synchronized int hash() {
    currentHash++;
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
    if (event instanceof MouseEvent) {
      dispatchMouseEvent((MouseEvent) event);
    } else if (event instanceof KeyEvent) {
      dispatchKeyEvent((KeyEvent) event);
    } else if (event instanceof CharEvent) {
      onCharEvent((CharEvent) event);
    }
  }

  public interface OnClickListener {
    void onClick(View view);
  }
}