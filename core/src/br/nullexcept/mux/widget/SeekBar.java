package br.nullexcept.mux.widget;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.graphics.*;
import br.nullexcept.mux.graphics.drawable.ColorDrawable;
import br.nullexcept.mux.input.KeyEvent;
import br.nullexcept.mux.input.MotionEvent;
import br.nullexcept.mux.input.MouseEvent;
import br.nullexcept.mux.lang.Function3;
import br.nullexcept.mux.res.AttributeList;
import br.nullexcept.mux.view.AttrList;
import br.nullexcept.mux.view.View;

public class SeekBar extends View {
    private int progress;
    private int max;

    private Drawable trackDrawable = new ColorDrawable(Color.RED);
    private Drawable thumbDrawable  = new ColorDrawable(Color.GREEN);
    private Drawable progressDrawable = new ColorDrawable(Color.BLUE);
    private final Rect rect = new Rect();
    private Function3<SeekBar, Boolean, Integer> seekListener;
    protected boolean captureScrollPosition = false;
    protected int scrollY = 0;

    public SeekBar(Context context) {
        this(context, null);
    }

    public SeekBar(Context context, AttributeList attrs) {
        super(context, attrs);
        AttributeList init = initialAttributes();
        init.searchFloat(AttrList.max, x -> setMax(x.intValue()));
        init.searchFloat(AttrList.progress, x -> setProgress(x.intValue()));
        init.searchDrawable(AttrList.progressDrawable, this::setProgressDrawable);
        init.searchDrawable(AttrList.thumbDrawable, this::setThumbDrawable);
        init.searchDrawable(AttrList.trackDrawable, this::setTrackDrawable);

        setFocusable(true);
    }

    private void seek(int value, boolean user) {
        value = Math.min(max, Math.max(0, value));
        if (value != progress) {
            this.progress = value;
            invalidate();
            if (seekListener != null) {
                seekListener.run(this, user, value);
            }
        }
    }

    // (View, FromUser, Value)
    public void setOnSeekListener(Function3<SeekBar, Boolean, Integer> listener) {
        seekListener = listener;
    }

    @Override
    protected void onKeyEvent(KeyEvent keyEvent) {
        int percent = (int) (max * 0.02);
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.KEY_UP:
            case KeyEvent.KEY_RIGHT:
                seek(progress+percent, true);
                break;
            case KeyEvent.KEY_LEFT:
            case KeyEvent.KEY_DOWN:
                seek(progress-percent, true);
                break;
            default:
                System.err.println("DEF");
                super.onKeyEvent(keyEvent);
                break;
        }
    }

    @Override
    protected boolean dispatchMouseEvent(MouseEvent event) {
        if (isFocused()) {
            if (!captureScrollPosition) {
                captureScrollPosition = true;
                scrollY = event.getScroll().y;
                return super.dispatchMouseEvent(event);
            } else {
                int y = event.getScroll().y - scrollY;
                scrollY = event.getScroll().y;
                if (y != 0) {
                    int percent = (int) (max * 0.02);
                    seek(progress+(percent * (y < 0 ? -1 : 1)), true);
                }
            }
        }
        return super.dispatchMouseEvent(event);
    }

    @Override
    public void onFocusChanged(boolean focused) {
        super.onFocusChanged(focused);
        captureScrollPosition = false;
    }

    @Override
    protected boolean onMouseEvent(MouseEvent mouseEvent) {
        float x = mouseEvent.getX();
        int w = (int) ((x/(double)getMeasuredWidth()) * max);
        seek(w, true);
        requestFocus();
        return mouseEvent.getAction() == MotionEvent.ACTION_DOWN;
    }

    public void setMax(int max) {
        this.max = Math.max(1, max);
        setProgress(progress);
        invalidate();
    }

    public void setProgress(int progress) {
        seek(progress, false);
    }

    public void setTrackDrawable(Drawable trackDrawable) {
        this.trackDrawable = trackDrawable;
        changeDrawableState();
        measure();
        invalidate();
    }

    public void setThumbDrawable(Drawable thumbDrawable) {
        this.thumbDrawable = thumbDrawable;
        changeDrawableState();
        measure();
        invalidate();
    }

    public void setProgressDrawable(Drawable progressDrawable) {
        this.progressDrawable = progressDrawable;
        changeDrawableState();
        measure();
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int thumbOffset = 0;
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        if (thumbDrawable != null) {
            thumbOffset = thumbDrawable.getWidth() / 2;
        }
        if (trackDrawable != null) {
            rect.setPosition(thumbOffset, (height-trackDrawable.getHeight())/2);
            rect.setSize(Math.max(1, width-(thumbOffset*2)), trackDrawable.getHeight());
            trackDrawable.setBounds(rect);
            trackDrawable.draw(canvas);
        }
        if (progressDrawable != null) {
            rect.setPosition(thumbOffset, (height-progressDrawable.getHeight())/2);
            int w = (width-(thumbOffset*2));
            w = (int) Math.round(w * ((double)progress/max));
            rect.setSize(Math.max(1, w), progressDrawable.getHeight());
            progressDrawable.setBounds(rect);
            progressDrawable.draw(canvas);
        }
        if (thumbDrawable != null) {
            int x = (int) (Math.max(1, width-(thumbOffset*2)) * ((double)progress/max));
            int y = (height - thumbDrawable.getHeight()) / 2;
            rect.setPosition(x, y);
            rect.setSize(thumbDrawable.getWidth(), thumbDrawable.getHeight());
            thumbDrawable.setBounds(rect);
            thumbDrawable.draw(canvas);
        }
    }

    @Override
    protected Size onMeasureContent(int parentWidth, int parentHeight) {
        Size def = super.onMeasureContent(parentWidth, parentHeight);
        int mh = 0;
        if (thumbDrawable != null) {
            mh = Math.max(thumbDrawable.getHeight(), mh);
        }
        if (trackDrawable != null) {
            mh = Math.max(trackDrawable.getHeight(), mh);
        }
        if (progressDrawable != null) {
            mh = Math.max(progressDrawable.getHeight(), mh);
        }
        def.height += mh * 2;
        return def;
    }

    @Override
    protected void changeDrawableState() {
        if (thumbDrawable != null) {
            if (thumbDrawable.setState(getStateList())) {
                invalidate();
            }
        }
        if (trackDrawable != null) {
            if (trackDrawable.setState(getStateList())) {
                invalidate();
            }
        }
        if (progressDrawable != null) {
            if (progressDrawable.setState(getStateList())) {
                invalidate();
            }
        }
        super.changeDrawableState();
    }
}
