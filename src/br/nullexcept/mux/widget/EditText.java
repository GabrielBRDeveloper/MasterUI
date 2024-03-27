package br.nullexcept.mux.widget;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.graphics.Canvas;
import br.nullexcept.mux.graphics.Color;
import br.nullexcept.mux.graphics.Paint;
import br.nullexcept.mux.graphics.Point;
import br.nullexcept.mux.graphics.fonts.FontMetrics;
import br.nullexcept.mux.input.CharEvent;
import br.nullexcept.mux.input.KeyEvent;
import br.nullexcept.mux.input.MouseEvent;
import br.nullexcept.mux.lang.TextLayout;
import br.nullexcept.mux.res.AttributeList;
import br.nullexcept.mux.view.AttrList;
import br.nullexcept.mux.view.View;

public class EditText extends View {
    private final TextLayout text = new TextLayout();
    private final Paint paint = new Paint();
    private int selectionColor = Color.RED;
    private int textColor = Color.GREEN;
    private boolean caretVisible = false;
    private boolean singleLine = false;
    private boolean loopState = false;
    private long mouseDownTime = 0L;
    private final Point mouseDownPoint = new Point();

    public EditText(Context context) {
        super(context);
    }

    public EditText(Context context, AttributeList attrs) {
        super(context, attrs);
    }

    {
        setFocusable(true);
        setOnClickListener(v-> requestFocus());
    }

    @Override
    protected void onInflate(AttributeList attr) {
        super.onInflate(attr);
        attr.searchText(AttrList.text, this::setText);
        attr.searchColor(AttrList.textColor, this::setTextColor);
        attr.searchColor(AttrList.selectionColor, this::setSelectionColor);
        attr.searchBoolean(AttrList.singleLine, this::setSingleLine);
        attr.searchDimension(AttrList.textSize, this::setTextSize);
    }

    @Override
    protected boolean onMouseEvent(MouseEvent mouseEvent) {
        if (mouseEvent.getDownTime() != mouseDownTime){
            mouseDownTime = mouseEvent.getDownTime();
            mouseDownPoint.set(Math.round(mouseEvent.getX()), Math.round(mouseEvent.getY()));
        }

        long downTime = (System.nanoTime() - mouseEvent.getDownTime())/1000;

        if (downTime < 200 && mouseEvent.getAction() == MouseEvent.ACTION_UP){
            text.setSelection(getCharIndex(Math.round(mouseEvent.getX()), Math.round(mouseEvent.getY())));
        } else if (downTime > 200){
            text.setSelection(
                    getCharIndex(mouseDownPoint.x, mouseDownPoint.y),
                    getCharIndex(Math.round(mouseEvent.getX()), Math.round(mouseEvent.getY()))
            );
        }
        invalidate();
        return super.onMouseEvent(mouseEvent);
    }

    private int getCharIndex(int x, int y){
        y -= getPaddingTop();
        x -= getPaddingLeft();
        int line = (int)(y/font().getLineHeight());

        line = Math.max(0, Math.min(text.getLineCount(), line));

        int start = text.getLineStart(line);
        if (start == text.length()) {
            return text.length();
        }
        int end = text.getLineEnd(line);
        int px = 0;
        while (start < end){
            int w = (int) font().measureChar(text.charAt(start));
            if (px + w > x){
                break;
            }
            px += w;
            start++;
        }
        return start;
    }

    private FontMetrics font() {
        return paint.getFontMetrics();
    }

    private void caretLoop() {
        loopState = isVisible() && isFocused();
        invalidate();
        caretVisible = (!caretVisible) && isFocused();
        if (loopState) {
            post(this::caretLoop, 300);
        }
    }

    public void setTextColor(int color) {
        textColor = color;
        invalidate();
    }

    public void setSelectionColor(int selectionColor) {
        this.selectionColor = selectionColor;
        invalidate();
    }

    public void setTextSize(float size) {
        paint.setTextSize(size);
        if (getLayoutParams().hasWrap())
            measure();
        invalidate();
    }

    public void setSingleLine(boolean singleLine) {
        this.singleLine = singleLine;
        if (singleLine) {
            text.removeBreaks();
        }
    }

    @Override
    protected int calculateHeight() {
        int lineCount = Math.max(1, text.getLineCount());
        return (int) ((lineCount * font().getLineHeight()) + getPaddingTop() + getPaddingBottom());
    }

    @Override
    protected int calculateWidth() {
        int width = 0;
        int line = 0;
        for (int i = 0; i < text.getLineCount(); i++) {
            int lw = text.getLineLength(i);
            if (lw > width) {
                line = i;
                width = lw;
            }
        }
        width = 0;
        for (int i = text.getLineStart(line); i < text.getLineEnd(line); i++){
            width += font().measureChar(text.charAt(i));
        }

        return width + getPaddingLeft() + getPaddingRight();
    }

    public CharSequence getText() {
        return text;
    }

    public void setText(CharSequence text) {
        this.text.delete(0, this.text.length());
        this.text.insert(0, text);
        updateText();
    }

    @Override
    protected void onCharEvent(CharEvent charEvent) {
        super.onCharEvent(charEvent);
        if (text.getSelectionLength() > 0){
            text.delete();
        }
        text.insert(String.valueOf(charEvent.getCharacter()));
        updateText();
    }

    @Override
    protected void onKeyEvent(KeyEvent keyEvent) {
        super.onKeyEvent(keyEvent);
        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEY_ENTER:
                case KeyEvent.KEY_KP_ENTER:
                    if (!singleLine) {
                        text.insert("\n");
                        updateText();
                    }
                    break;
                case KeyEvent.KEY_A:
                    if (keyEvent.hasCtrl()){
                        text.setSelection(0, text.length());
                    }
                    break;
                case KeyEvent.KEY_RIGHT:
                    if (keyEvent.hasShift()) {
                        text.setSelection(text.getSelectionStart(), text.getSelectionEnd() + 1);
                    } else {
                        text.setSelection(text.getSelectionEnd() + 1);
                    }
                    invalidate();
                    break;
                case KeyEvent.KEY_LEFT:
                    if (keyEvent.hasShift()) {
                        text.setSelection(text.getSelectionStart(), text.getSelectionEnd() - 1);
                    } else {
                        text.setSelection(text.getSelectionEnd() - 1);
                    }
                    invalidate();
                    break;
                case KeyEvent.KEY_BACKSPACE:
                    if (text.getSelectionStart() > 0 || text.getSelectionLength() > 1) {
                        text.delete();
                        updateText();
                    }
                    break;
            }
        }
    }

    protected void updateText() {
        if (getLayoutParams().hasWrap()) {
            measure();
        }
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(getPaddingLeft(), getPaddingTop());
        if (!loopState){
            caretLoop();
        }
        FontMetrics metrics = paint.getFontMetrics();
        int lineHeight = (int) metrics.getLineHeight();
        int y = 0;
        if (text.getSelectionLength() == 0) {
            drawCaret(canvas);
        } else {
            drawSelection(canvas);
        }
        paint.setColor(textColor);
        for (int i = 0; i < text.getLineCount(); i++) {
            drawLine(canvas, 0, y, i);
            y += lineHeight;
        }
        canvas.translate(-getPaddingLeft(), -getPaddingTop());
    }

    private void drawSelection(Canvas canvas) {
        int ls = text.getLineIndex(text.getSelectionStart());
        int le = text.getLineIndex(text.getSelectionEnd());
        paint.setColor(selectionColor);
        if (ls == le){
            drawSelection(canvas, text.getSelectionStart(), text.getSelectionEnd(),Math.round(ls * font().getLineHeight()));
        } else {
            int y = Math.round(font().getLineHeight()*ls);
            drawSelection(
                    canvas,
                    text.getSelectionStart(),
                    text.getLineEnd(ls),
                    y
            );

            y += font().getLineHeight();
            ls++;
            while (ls != le){
                drawSelection(
                        canvas,
                        text.getLineStart(ls),
                        text.getLineEnd(ls),
                        y
                );
                ls++;
                y += font().getLineHeight();
            }

            drawSelection(canvas,
                    text.getLineStart(ls),
                    text.getSelectionEnd(),
                    y);
        }
    }

    private void drawSelection(Canvas canvas, int start, int end, int y){
        int l = text.getLineStart(text.getLineIndex(start));
        int x = textWidth(l, start);
        int w = textWidth(start, end);
        if (w == 0)
            w = 1;

        canvas.drawRect(x, y, x+w, y + font().getLineHeight(), paint);
    }

    private int textWidth(int start, int end){
        int w = 0;
        while (end > start){
            w += font().measureChar(text.charAt(start));
            start++;
        }
        return w;
    }

    private void drawCaret(Canvas canvas) {
        if ((!caretVisible)) return;
        int w = (int) (font().measureChar('l') / 3);
        int x = 0, y = 0;

        int line = text.getLineIndex(text.getSelectionStart()-1);
        int start = text.getLineStart(line);
        while (start < text.getSelectionStart()) {
            x += font().measureChar(text.charAt(start));
            start++;
        }
        y += line * font().getLineHeight();
        canvas.drawRect(x - (w / 2), y, x + w, (int) (y + font().getLineHeight()), paint);
    }

    private void drawLine(Canvas canvas, int x, int y, int line) {
        int start = text.getLineStart(line);
        if (line > 0) start++;
        int end = text.getLineEnd(line);
        int ascent = (int) font().getAscent();
        while (start < end) {
            canvas.drawText(String.valueOf(text.charAt(start)), x, y + ascent, paint);
            x += font().measureChar(text.charAt(start));
            start++;
        }
    }
}