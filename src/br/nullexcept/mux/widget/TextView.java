package br.nullexcept.mux.widget;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.graphics.Canvas;
import br.nullexcept.mux.graphics.Color;
import br.nullexcept.mux.graphics.Paint;
import br.nullexcept.mux.graphics.Rect;
import br.nullexcept.mux.res.AttributeList;
import br.nullexcept.mux.text.Editable;
import br.nullexcept.mux.text.TextLayout;
import br.nullexcept.mux.view.View;
import br.nullexcept.mux.view.AttrList;

public class TextView extends View {
    private final Paint paint = new Paint();
    private final Editable text = new Editable();
    private final TextLayout layout = new TextLayout(text, paint, new TextLayout.TextDrawer() {
        @Override
        public void drawSelection(Canvas canvas, int x, int y, int width, int height) {
        }

        @Override
        public void drawCharacter(Canvas canvas, char ch, int x, int y) {
            canvas.drawText(String.valueOf(ch),x,y, paint);
        }

        @Override
        public void drawCaret(Canvas canvas, int x, int y) {

        }
    });

    public TextView(Context context) {
        this(context, null);
    }

    public TextView(Context context, AttributeList attrs) {
        super(context, attrs);
        attrs = initialAttributes();
        attrs.searchText(AttrList.text, this::setText);
        attrs.searchColor(AttrList.textColor, this::setTextColor);
        attrs.searchDimension(AttrList.textSize, this::setTextSize);
        layout.update();
    }

    @Override
    protected int calculateWidth() {
        return layout.getWrapSize().x + getPaddingLeft() + getPaddingRight();
    }

    @Override
    protected int calculateHeight() {
        return layout.getWrapSize().y + getPaddingTop() + getPaddingBottom();
    }

    public void setTextColor(int color){
        paint.setColor(color);
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            text.getSelection().set(0, text.length());
            layout.setGravity(getGravity());
            canvas.translate(getPaddingLeft(), getPaddingTop());
            layout.draw(canvas);
            canvas.translate(-getPaddingLeft(), -getPaddingTop());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setText(CharSequence text) {
        this.text.delete(0, text.length());
        this.text.insert(text);
        layout.update();
        if(getLayoutParams().hasWrap()){
            measure();
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int width, int height) {
        super.onMeasure(width, height);
        layout.measure(width-getPaddingLeft()-getPaddingRight(), height-getPaddingTop()-getPaddingBottom());
    }

    public void setTextSize(float textSize){
        this.paint.setTextSize(textSize);
        layout.update();
        invalidate();
    }

    public CharSequence getText() {
        return text;
    }
}
