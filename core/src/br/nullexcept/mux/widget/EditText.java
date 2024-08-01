package br.nullexcept.mux.widget;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.app.applets.ClipboardApplet;
import br.nullexcept.mux.graphics.*;
import br.nullexcept.mux.graphics.fonts.Typeface;
import br.nullexcept.mux.input.CharEvent;
import br.nullexcept.mux.input.KeyEvent;
import br.nullexcept.mux.input.MotionEvent;
import br.nullexcept.mux.input.MouseEvent;
import br.nullexcept.mux.res.AttributeList;
import br.nullexcept.mux.text.Editable;
import br.nullexcept.mux.text.OnTextChangedListener;
import br.nullexcept.mux.text.TextLayout;
import br.nullexcept.mux.view.AttrList;
import br.nullexcept.mux.view.View;
import br.nullexcept.mux.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

public class EditText extends View {
    private final Paint paint = new Paint();
    private final Paint paintSelection = new Paint();
    private ColorStateList textColor = new ColorStateList(Color.RED);
    private ColorStateList hintColor = new ColorStateList(Color.RED);
    private ColorStateList selectionColor = new ColorStateList(Color.RED);
    private OnTextChangedListener textChangedListener = null;
    private final Editable text = new Editable(this::onTextChanged);
    private final Editable hint = new Editable(this::onTextChanged);

    private final TextDrawing defaultRenderer = new TextDrawing(paint);
    private final TextMeasureRenderer measureRenderer = new TextMeasureRenderer();

    private final TextLayout textLayout = new TextLayout(text, paint, defaultRenderer, 65536); // 1MB for each EditText, for edit 65.536 lines
    private final TextLayout hintLayout = new TextLayout(hint, paint, defaultRenderer);

    private final Point mouseDown = new Point();
    private boolean mouseFind = false;
    private boolean singleLine = false;
    private final Size textViewport = new Size();
    private int mouseCaret;
    protected boolean editable = true;

    public EditText(Context context) {
        this(context, null);
    }

    public EditText(Context context, AttributeList init) {
        super(context, init);
        setFocusable(true);
        setOnClickListener(view -> requestFocus());
        AttributeList attrs = initialAttributes();
        attrs.searchColorList(AttrList.textColor, this::setTextColor);
        attrs.searchDimension(AttrList.textSize, this::setTextSize);
        attrs.searchBoolean(AttrList.singleLine, this::setSingleLine);
        attrs.searchBoolean(AttrList.editable, this::setEditable);
        attrs.searchColorList(AttrList.selectionColor, this::setSelectionColor);
        attrs.searchText(AttrList.text, this::setText);
        attrs.searchText(AttrList.hint, this::setHint);

        attrs.searchColorList(AttrList.hintColor, this::setHintColor);

        {   // Customize typeface
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
    }

    public void setOnTextChangedListener(OnTextChangedListener textChangedListener) {
        this.textChangedListener = textChangedListener;
    }

    public void setHintColor(ColorStateList hintColor) {
        this.hintColor = hintColor;
        if (text.length() == 0) {
            invalidate();
        }
    }

    public void setHint(CharSequence text) {
        hint.delete(0, hint.length());
        hint.insert(preFormatText(text));
        hint.getSelection().set(0);
        if (text.length() == 0) {
            measureText(true);
            checkMeasure();
            invalidate();
        }
    }

    public void setSelectionColor(ColorStateList color) {
        this.selectionColor = color;
        invalidate();
    }

    public void setSingleLine(boolean singleLine) {
        this.singleLine = singleLine;
        setText(text);
    }

    private String preFormatText(CharSequence from) {
        String text = from.toString();
        formatUnixText(text);
        if (singleLine) {
            while (text.contains("\n") || text.contains("\r")) {
                text = text.replaceAll("\n","").replaceAll("\r","");
            }
        }
        return text;
    }

    public void setText(CharSequence value) {
        this.text.delete(0, this.text.length());
        String text = preFormatText(value);
        this.text.insert(text);
        changeText();
        invalidate();
    }

    private String formatUnixText(String text) {
        while (text.contains("\r\n")) {
            text = text.replace("\r\n", "\n");
        }

        while (text.contains("\r")) {
            text = text.replace("\r", "\n");
        }

        return text;
    }

    private void changeText() {
        checkMeasure();
        measureText(true);
        invalidate();
    }

    public void setTypeface(Typeface typeface) {
        paint.setTypeface(typeface);
        measureText(true);
        checkMeasure();
        invalidate();
    }

    public void setTextSize(float value) {
        paint.setTextSize(value);
        measureText(true);
        checkMeasure();
        invalidate();
    }

    public void setTextColor(ColorStateList color) {
        this.textColor = color;
        invalidate();
    }

    private void onTextChanged(Editable text) {
        if (textChangedListener != null) {
            textChangedListener.onContentChanged(text);
        }
    }

    public Editable getText() {
        return text;
    }

    public CharSequence getHint() {
        return hint;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public void setGravity(int gravity) {
        super.setGravity(gravity);
        measureText(true);
        checkMeasure();
    }

    @Override
    protected void onCharEvent(CharEvent charEvent) {
        if ((!editable) || singleLine && (charEvent.getCharacter() == '\n' || charEvent.getCharacter() == '\r')) {
            return;
        }
        if (text.getSelection().length() > 0) {
            text.delete();
        }
        text.insert(charEvent.getCharacter()+"");
        changeText();
        invalidate();
    }



    @Override
    protected void changeDrawableState() {
        if (selectionColor.setState(getStateList()) || textColor.setState(getStateList())){
            invalidate();
        }
        super.changeDrawableState();
    }

    private long lastMouseDown = -1;
    private int lastDownStart = 0;

    @Override
    protected boolean onMouseEvent(MouseEvent mouseEvent) {
        if (mouseEvent.getAction() == MotionEvent.ACTION_DOWN && mouseEvent.getButton() == MouseEvent.LEFT_BUTTON) {
            mouseDown.set((int) mouseEvent.getX()-getPaddingLeft(), (int) mouseEvent.getY()-getPaddingTop());
            mouseCaret = 0;
            mouseFind = false;

            measureText(true);
            textLayout.setRenderer(measureRenderer);
            textLayout.draw(null, textViewport.width,textViewport.height, false);
            textLayout.setRenderer(defaultRenderer);
            if (lastMouseDown == mouseEvent.getDownTime()) {
                text.getSelection().set(lastDownStart, mouseCaret);
            } else {
                lastDownStart = mouseCaret;
                text.getSelection().set(mouseCaret);
            }

            lastMouseDown = mouseEvent.getDownTime();
            invalidate();
        }
        return super.onMouseEvent(mouseEvent);
    }

    @Override
    protected void onKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN)
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.KEY_DELETE: {
                if (!editable) break;
                int low = text.getSelection().low();
                if (low > 0 && text.getSelection().length() == 0) {
                    text.delete(low - 1, low);
                    changeText();
                } else if (text.getSelection().length() > 0) {
                    text.delete();
                    changeText();
                }
            }break;
            case KeyEvent.KEY_V: {
                if (keyEvent.hasCtrl()) {
                    String content = ((ClipboardApplet)getContext().getApplet(Context.CLIPBOARD_APPLET)).getContent();
                    if (content == null) break;
                    if (text.getSelection().length() > 0) {
                        text.delete();
                    }
                    text.insert(preFormatText(content));
                    changeText();
                }
            } break;
            case KeyEvent.KEY_A: {
                if (keyEvent.hasCtrl()) {
                    text.getSelection().set(0, text.length());
                    invalidate();
                }
            } break;
            case KeyEvent.KEY_LEFT: {
                int end = text.getSelection().end();
                if (end > 0 || text.getSelection().length() > 0) {
                    if (keyEvent.hasShift()) {
                        text.getSelection().set(text.getSelection().start(), end - 1);
                    } else {
                        text.getSelection().set(end - 1);
                    }
                    invalidate();
                }
            }break;
            case KeyEvent.KEY_RIGHT: {
                int end = text.getSelection().end();
                if (end < text.length() || text.getSelection().length() > 0) {
                    if (keyEvent.hasShift()) {
                        text.getSelection().set(text.getSelection().start(), end + 1);
                    } else {
                        text.getSelection().set(end + 1);
                    }
                    invalidate();
                }
            }break;
            case KeyEvent.KEY_BACKSPACE:
                if (!editable) break;
                text.delete();
                changeText();
                break;
            case KeyEvent.KEY_ENTER:
            case KeyEvent.KEY_KP_ENTER:
                if (!editable || singleLine) break;
                text.insert("\n");
                changeText();
                break;
        }
    }

    private void checkMeasure() {
        if (getLayoutParams().hasWrap()) {
            measure();
        }
    }

    private void measureText(boolean force) {
        measureText(force, getMeasuredWidth(), getMeasuredHeight());
    }

    private void measureText(boolean force, int width, int height) {
        int mw = (int) (width - getPaddingLeft() - getPaddingRight() - paint.getFontMetrics().measureText("   "));
        int mh = (int) (width - getPaddingTop() - getPaddingBottom() - paint.getFontMetrics().measureText("   "));
        mw = Math.max(1, mw);
        mh = Math.max(1, mh);
        if (force || (textViewport.width != mw || textViewport.height != mw)) {
            textViewport.set(mw,mh);
            textLayout.measure(mw,mh, getGravity());
            if (text.length() == 0) {
                hintLayout.measure(mw, mh, getGravity());
            }
        }
    }

    @Override
    protected void onMeasure(int width, int height) {
        super.onMeasure(width, height);
    }

    protected void onDrawCharacter(Canvas canvas, char ch, int x, int y, int charIndex, int lineStart, int lineEnd) {
        canvas.drawText(String.valueOf(ch), x, y, paint);
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

        measureText(true, w,h);

        return new Size(currentLayout().getWrapSize());
    }

    private TextLayout currentLayout() {
        return text.length() == 0 ? hintLayout : textLayout;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        measureText(false);
        canvas.translate(getPaddingLeft(), getPaddingTop());
        paint.setColor((text.length() == 0 ? hintColor : textColor).getColor());
        currentLayout().draw(canvas,textViewport.width,textViewport.height,true);
        canvas.translate(-getPaddingLeft(), -getPaddingTop());
    }

    private class TextMeasureRenderer implements TextLayout.TextRenderer {

        @Override public void drawSelection(Canvas canvas, int x, int y, int width, int height) {}
        @Override public void drawCaret(Canvas canvas, int x, int y) {}

        @Override
        public void drawCharacter(Canvas canvas, char ch, int x, int y, int charIndex, int lineStart, int lineEnd) {
            if (mouseFind) return;
            mouseCaret = charIndex;
            if (y >= mouseDown.y || lineEnd == text.length()) {
                float width = paint.getFontMetrics().measureChar(ch);

                if(x+(width/2) >= mouseDown.x) {
                    mouseFind = true;
                } else if (charIndex + 1 >= lineEnd) {
                    mouseCaret++;
                    mouseFind = true;
                }
            }
        }

    }

    private class TextDrawing implements TextLayout.TextRenderer {
        private final Paint paint;

        private TextDrawing(Paint paint) {
            this.paint = paint;
        }

        @Override
        public void drawSelection(Canvas canvas, int x, int y, int width, int height) {
            paintSelection.setColor(selectionColor.getColor());
            canvas.drawRect(x,y,x+width,y+height, paintSelection);
        }

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
        public void drawCaret(Canvas canvas, int x, int y) {
            paintSelection.setColor(selectionColor.getColor());
            canvas.drawRect(x,y,x+(paint.getFontMetrics().measureChar('|')/4),y+paint.getFontMetrics().getLineHeight(),paintSelection);
        }
    }
}