package br.nullexcept.mux.widget;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.graphics.*;
import br.nullexcept.mux.res.AttributeList;
import br.nullexcept.mux.text.Editable;
import br.nullexcept.mux.text.TextLayout;
import br.nullexcept.mux.view.AttrList;
import br.nullexcept.mux.view.View;
import br.nullexcept.mux.view.ViewGroup;

public class TextView extends View {
    private final Paint paint = new Paint();
    private ColorStateList textColor = new ColorStateList(Color.RED);
    private final Editable text = new Editable();
    private final TextLayout layout = new TextLayout(text, paint, new SimpleTextRenderer(paint));

    public TextView(Context context) {
        this(context, null);
    }

    public TextView(Context context, AttributeList attrs) {
        super(context, attrs);
        attrs = initialAttributes();
        attrs.searchText(AttrList.text, this::setText);
        attrs.searchColor(AttrList.textColor, this::setTextColor);
        attrs.searchDimension(AttrList.textSize, this::setTextSize);
    }

    @Override
    protected Size onMeasureContent(int parentWidth, int parentHeight) {
        ViewGroup.LayoutParams params = getLayoutParams();
        int w = Integer.MAX_VALUE;
        int h = Integer.MAX_VALUE;

        if (params.width == FrameLayout.LayoutParams.MATCH_PARENT) {
            w = parentWidth;
        }
        if (params.height == FrameLayout.LayoutParams.MATCH_PARENT) {
            h = parentHeight;
        }

        layout.measure(w,h, getGravity());

        return new Size(layout.getWrapSize());
    }

    public void setTextColor(int color){
        textColor = new ColorStateList(color);
        invalidate();
    }

    @Override
    protected void changeDrawableState() {
        super.changeDrawableState();
        if (textColor.setState(getStateList())) {
            invalidate();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();

        canvas.translate(getPaddingLeft(), getPaddingTop());
        paint.setColor(textColor.getColor());
        layout.draw(canvas, width, height, false);
        canvas.translate(-getPaddingLeft(), -getPaddingTop());
    }

    @Override
    protected void onMeasure(int width, int height) {
        super.onMeasure(width, height);
        layout.measure(width, height, getGravity());
    }

    public void setText(CharSequence text) {
        this.text.delete(0, this.text.length());
        this.text.insert(text);
        layout.update();

        if(getLayoutParams().hasWrap()){
            measure();
        }
        invalidate();
    }

    public void setTextSize(float textSize){
        this.paint.setTextSize(textSize);
        layout.update();
        measure();
        invalidate();
    }

    public CharSequence getText() {
        return text;
    }
}

class SimpleTextRenderer implements TextLayout.TextRenderer {
    private final Paint paint;

    SimpleTextRenderer(Paint paint) {
        this.paint = paint;
    }

    @Override
    public void drawSelection(Canvas canvas, int x, int y, int width, int height) {}

    @Override
    public void drawCharacter(Canvas canvas, char ch, int x, int y) {
        switch (ch) {
            case '\n':
            case '\r':
            case ' ':
            case '\t':
            case '\f':
                break;
            default:
                canvas.drawText(String.valueOf(ch), x, y, paint);
                break;
        }
    }

    @Override
    public void drawCaret(Canvas canvas, int x, int y) {}
}
