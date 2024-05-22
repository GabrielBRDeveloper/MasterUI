package br.nullexcept.mux.widget;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.graphics.Canvas;
import br.nullexcept.mux.graphics.Drawable;
import br.nullexcept.mux.graphics.Rect;
import br.nullexcept.mux.graphics.Size;
import br.nullexcept.mux.res.AttributeList;
import br.nullexcept.mux.view.AttrList;
import br.nullexcept.mux.view.Gravity;
import br.nullexcept.mux.view.View;

public class ImageView extends View {
    private Drawable image;
    private final Rect rect = new Rect();

    public ImageView(Context context) {
        this(context, null);
    }

    public ImageView(Context context, AttributeList attrs) {
        super(context, attrs);
        attrs = initialAttributes();
        attrs.searchDrawable(AttrList.src, this::setImageDrawable);
    }

    public void setImageDrawable(Drawable image) {
        this.image = image;
        measure();
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getMeasuredWidth()-getPaddingLeft()-getPaddingRight();
        float h = getMeasuredHeight()-getPaddingTop()-getPaddingBottom();
        float iw, ih;

        if (image != null){
            float pw = w/image.getWidth();
            if (pw * image.getHeight() > h){
                ih = h;
                iw = (h/image.getHeight()) * image.getWidth();
            } else {
                iw = w;
                ih = pw * image.getHeight();
            }
            Gravity.applyGravity(getGravity(),Math.round(iw),Math.round(ih),Math.round(w), Math.round(h),rect);
            rect.move(getPaddingLeft(), getPaddingTop());
            image.setBounds(rect);
            image.draw(canvas);
        }
    }

    @Override
    protected Size onMeasureContent(int parentWidth, int parentHeight) {
        if (image != null) {
            return new Size(image.getWidth(), image.getHeight());
        } else {
            return super.onMeasureContent(parentWidth, parentHeight);
        }
    }
}
