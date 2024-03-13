package br.nullexcept.mux.widget;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.graphics.Canvas;
import br.nullexcept.mux.graphics.Paint;
import br.nullexcept.mux.graphics.Rect;
import br.nullexcept.mux.graphics.fonts.FontMetrics;
import br.nullexcept.mux.res.AttributeList;
import br.nullexcept.mux.view.Gravity;
import br.nullexcept.mux.view.View;

public class TextView extends View {
    private final Paint paint = new Paint();
    private final Rect rect = new Rect();
    private String[] text = new String[0];

    public TextView(Context context) {
        super(context);
    }

    public TextView(Context context, AttributeList attrs) {
        super(context, attrs);
    }

    @Override
    protected int calculateWidth() {
        FontMetrics metrics = paint.getFontMetrics();
        int width = 0;
        for (String line : text){
            width = Math.max((int) metrics.measureText(line), width);
        }
        return width + getPaddingLeft() + getPaddingRight();
    }

    @Override
    protected int calculateHeight() {
        return (int) (paint.getFontMetrics().getLineHeight() * text.length) + getPaddingTop() + getPaddingBottom();
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
        Gravity.applyGravity(getGravity(), 1, lineHeight*text.length, width, height, rect);
        int y = rect.top + getPaddingTop();
        for (String line: text){
            Gravity.applyGravity(getGravity(), (int) metrics.measureText(line), lineHeight, width, height, rect);
            canvas.drawText(line, rect.left+getPaddingLeft(), (int) (y+Math.abs(metrics.getAscent())), paint);
            y+= lineHeight;
        }
    }

    public void setText(String text) {
        this.text = String.valueOf(text).split("\n");
        measure();
        invalidate();
    }

    public void setTextSize(float textSize){
        this.paint.setTextSize(textSize);
        invalidate();
    }
}
