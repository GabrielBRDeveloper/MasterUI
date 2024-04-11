package br.nullexcept.mux.widget;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.graphics.Canvas;
import br.nullexcept.mux.graphics.Paint;
import br.nullexcept.mux.graphics.Rect;
import br.nullexcept.mux.graphics.Size;
import br.nullexcept.mux.graphics.fonts.FontMetrics;
import br.nullexcept.mux.res.AttributeList;
import br.nullexcept.mux.view.AttrList;
import br.nullexcept.mux.view.Gravity;
import br.nullexcept.mux.view.View;

public class TextView extends View {
    private final Paint paint = new Paint();
    private final Rect rect = new Rect();
    private CharSequence text = "";
    private String[] lines = new String[0];

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
    protected Size onMeasureContent() {
        Size size = new Size();
        FontMetrics metrics = paint.getFontMetrics();
        for (String line : lines){
            size.width = Math.max((int) metrics.measureText(line), size.width);
        }
        size.height = Math.round(paint.getFontMetrics().getLineHeight()*lines.length);
        return size;
    }

    public void setTextColor(int color){
        paint.setColor(color);
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();

        FontMetrics metrics = paint.getFontMetrics();
        int lineHeight = (int) metrics.getLineHeight();
        Gravity.applyGravity(getGravity(), 1, lineHeight* lines.length, width, height, rect);
        int y = rect.top + getPaddingTop();
        for (String line: lines){
            Gravity.applyGravity(getGravity(), (int) metrics.measureText(line), lineHeight, width, height, rect);
            canvas.drawText(line, rect.left+getPaddingLeft(), (int) (y+Math.abs(metrics.getAscent())), paint);
            y+= lineHeight;
        }
    }

    public void setText(CharSequence text) {
        this.text = text;
        this.lines = String.valueOf(text).split("\n");
        if(getLayoutParams().hasWrap()){
            measure();
        }
        invalidate();
    }

    public void setTextSize(float textSize){
        this.paint.setTextSize(textSize);
        invalidate();
    }

    public CharSequence getText() {
        return text;
    }
}
