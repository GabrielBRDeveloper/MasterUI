package br.nullexcept.mux.widget;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.graphics.Canvas;
import br.nullexcept.mux.graphics.Drawable;
import br.nullexcept.mux.graphics.Paint;
import br.nullexcept.mux.graphics.Rect;
import br.nullexcept.mux.res.AttributeList;
import br.nullexcept.mux.view.AttrList;
import br.nullexcept.mux.view.Gravity;
import br.nullexcept.mux.view.View;

public class ImageView extends View {
    private Drawable image;
    private final Rect rect = new Rect();

    public ImageView(Context context) {
        super(context);
    }

    public ImageView(Context context, AttributeList attrs) {
        super(context, attrs);
    }

    public void setImageDrawable(Drawable image) {
        this.image = image;
        measure();
        invalidate();
    }

    @Override
    protected void onInflate(AttributeList attr) {
        super.onInflate(attr);
        attr.searchDrawable(AttrList.src, this::setImageDrawable);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = canvas.getWidth();
        float h = canvas.getHeight();
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
            image.setBounds(rect);
            image.draw(canvas);
        }
    }

    @Override
    protected int calculateHeight() {
        return getPaddingBottom() + getPaddingTop() + image.getHeight();
    }

    @Override
    protected int calculateWidth() {
        return getPaddingLeft() + getPaddingRight() + image.getWidth();
    }
}
