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
import com.sun.org.apache.xpath.internal.operations.String;

import java.util.Arrays;

public class ImageView extends View {
    public static final int SCALE_FILL = 0;
    public static final int SCALE_FIT = 1;
    public static final int SCALE_CROP = 2;
    public static final int SCALE_WRAP = 3;
    
    private Drawable image;
    private final Rect rect = new Rect();
    private int scaleType = SCALE_FIT;

    public ImageView(Context context) {
        this(context, null);
    }

    public ImageView(Context context, AttributeList attrs) {
        super(context, attrs);
        attrs = initialAttributes();
        attrs.searchDrawable(AttrList.src, this::setImageDrawable);
        attrs.searchRaw(AttrList.scaleType, value -> {
            setScaleType(Arrays.asList("fill", "fit", "crop", "wrap").indexOf(value.toLowerCase().trim()));
        });
    }

    public void setScaleType(int scaleType) {
        this.scaleType = scaleType;
        invalidate();
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
            switch (Math.max(0, Math.min(SCALE_WRAP, scaleType))) {
                case SCALE_FILL: {
                    iw = w;
                    ih = h;
                } break;
                case SCALE_FIT: {
                    float pw = w/image.getWidth();
                    if (pw * image.getHeight() > h){
                        ih = h;
                        iw = (h/image.getHeight()) * image.getWidth();
                    } else {
                        iw = w;
                        ih = pw * image.getHeight();
                    }
                } break;
                case SCALE_CROP: {
                    float pw = w/image.getWidth();
                    if (pw * image.getHeight() < h){
                        ih = h;
                        iw = (h/image.getHeight()) * image.getWidth();
                    } else {
                        iw = w;
                        ih = pw * image.getHeight();
                    }
                } break;
                default: {
                    iw = Math.max(0, image.getWidth());
                    ih = Math.max(0, image.getHeight());
                } break;
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
