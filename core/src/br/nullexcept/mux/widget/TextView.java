package br.nullexcept.mux.widget;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.graphics.*;
import br.nullexcept.mux.graphics.fonts.Typeface;
import br.nullexcept.mux.res.AttributeList;
import br.nullexcept.mux.text.Editable;
import br.nullexcept.mux.text.TextLayout;
import br.nullexcept.mux.view.AttrList;
import br.nullexcept.mux.view.View;
import br.nullexcept.mux.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

public class TextView extends View {
    private final Paint paint = new Paint();
    private ColorStateList textColor = new ColorStateList(Color.RED);
    private final Editable text = new Editable();
    private final TextLayout layout = new TextLayout(text, paint, new SimpleTextRenderer());

    public TextView(Context context) {
        this(context, null);
    }

    public TextView(Context context, AttributeList initial) {
        super(context, initial);
        AttributeList attrs = initialAttributes();
        attrs.searchText(AttrList.text, this::setText);
        attrs.searchColorList(AttrList.textColor, value -> textColor = value);
        attrs.searchDimension(AttrList.textSize, this::setTextSize);

        String[] fontFamily = new String[]{"default"};
        int[] fontStyle = new int[1];

        attrs.searchRaw(AttrList.fontFamily, value -> fontFamily[0] = value);
        attrs.searchRaw(AttrList.textStyle, value -> {
            List<String> values = Arrays.asList(value.toLowerCase().split("\\|"));
            if (values.contains("italic")) {
                fontStyle[0] |= Typeface.STYLE_ITALIC;
            }
            if (values.contains("bold")) {
                fontStyle[0] |= Typeface.STYLE_BOLD;
            }
        });

        Typeface font = context.getResources().getFont(fontFamily[0], fontStyle[0]);
        if (font != null) {
            setTypeface(font);
        }
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

    protected TextLayout getLayout() {
        return layout;
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
        if (text == null) {
            text = "";
        }
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

    public void setTypeface(Typeface font) {
        this.paint.setTypeface(font);
        setTextSize(paint.getTextSize());
    }

    protected void onDrawCharacter(Canvas canvas, char ch, int x, int y, int charIndex, int lineStart, int lineEnd) {
        canvas.drawText(String.valueOf(ch), x, y, paint);
    }

    public CharSequence getText() {
        return text;
    }

    private class SimpleTextRenderer implements TextLayout.TextRenderer {
        @Override
        public void drawSelection(Canvas canvas, int x, int y, int width, int height) {}

        @Override
        public void drawCharacter(Canvas canvas, char ch, int x, int y, int charIndex, int lineStart, int lineEnd) {
            switch (ch) {
                case '\n':
                case '\r':
                case ' ':
                case '\t':
                case '\f':
                    break;
                default:
                    onDrawCharacter(canvas, ch, x, y, charIndex, lineStart, lineEnd);
                    break;
            }
        }

        @Override
        public void drawCaret(Canvas canvas, int x, int y) {}
    }
}
