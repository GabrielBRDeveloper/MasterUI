package br.nullexcept.mux.widget;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.app.applets.ClipboardApplet;
import br.nullexcept.mux.graphics.*;
import br.nullexcept.mux.graphics.drawable.MaterialIconDrawable;
import br.nullexcept.mux.graphics.fonts.FontMetrics;
import br.nullexcept.mux.input.CharEvent;
import br.nullexcept.mux.input.KeyEvent;
import br.nullexcept.mux.input.MouseEvent;
import br.nullexcept.mux.res.AttributeList;
import br.nullexcept.mux.text.Editable;
import br.nullexcept.mux.text.TextLayout;
import br.nullexcept.mux.text.TextSelection;
import br.nullexcept.mux.view.AttrList;
import br.nullexcept.mux.view.Menu;
import br.nullexcept.mux.view.View;

public class EditText extends View {
    private final Editable text = new Editable();
    private final TextSelection selection = text.getSelection();
    private final Paint paint = new Paint();
    private int selectionColor = Color.RED;
    private int textColor = Color.GREEN;
    private boolean caretVisible = false;
    private boolean singleLine = false;
    private boolean loopState = false;
    private long mouseDownTime = 0L;
    private final Menu.Group menu;
    private final Point mouseDownPoint = new Point();
    private final TextLayout layout = new TextLayout(text, paint, new TextLayout.TextDrawer() {
        @Override
        public void drawSelection(Canvas canvas, int x, int y, int width, int height) {
            paint.setColor(selectionColor);
            canvas.drawRect(x,y,x+width,y+height, paint);
        }

        @Override
        public void drawCharacter(Canvas canvas, char ch, int x, int y) {
            paint.setColor(textColor);
            if (ch == '\n')return;
            canvas.drawText(String.valueOf(ch),x,y, paint);
        }

        @Override
        public void drawCaret(Canvas canvas, int x, int y) {
            caretVisible = true;
            int w = (int) font().measureChar('|');
            canvas.drawRect(x - (w / 2), y, x + w, (int) (y + font().getLineHeight()), paint);
        }
    });

    public EditText(Context context) {
        this(context, null);
    }

    public EditText(Context context, AttributeList attrs) {
        super(context, attrs);
        attrs = initialAttributes();

        attrs.searchText(AttrList.text, this::setText);
        attrs.searchColor(AttrList.textColor, this::setTextColor);
        attrs.searchColor(AttrList.selectionColor, this::setSelectionColor);
        attrs.searchBoolean(AttrList.singleLine, this::setSingleLine);
        attrs.searchDimension(AttrList.textSize, this::setTextSize);

        menu = new Menu.Group("");
        menu.add(new Menu.Item("copy", "Copy", new MaterialIconDrawable("content_copy", textColor)));
        menu.add(new Menu.Item("cut", "Cut", new MaterialIconDrawable("content_cut", textColor)));
        menu.add(new Menu.Item("paste", "Paste", new MaterialIconDrawable("content_paste", textColor)));
    }

    {
        setFocusable(true);
        setOnClickListener(v-> requestFocus());
    }

    @Override
    protected boolean onMouseEvent(MouseEvent mouseEvent) {
        if (mouseEvent.getDownTime() != mouseDownTime){
            mouseDownTime = mouseEvent.getDownTime();
            mouseDownPoint.set(Math.round(mouseEvent.getX()), Math.round(mouseEvent.getY()));
        }

        long downTime = (System.nanoTime() - mouseEvent.getDownTime())/1000;

        if (downTime < 200 && mouseEvent.getAction() == MouseEvent.ACTION_UP){
            selection.set(getCharIndex(Math.round(mouseEvent.getX()), Math.round(mouseEvent.getY())));
        } else if (downTime > 200){
            selection.set(
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
    public boolean onCreateContextMenu(Menu menu) {
        menu.setOnClickListener(item -> {
            ClipboardApplet clipboard = getContext().getApplet(Context.CLIPBOARD_APPLET);
            switch (item.getId()) {
                case "paste": {
                    pasteFromClipboard();
                }break;
                case "copy": {
                    copyToClipboard();
                }break;
                case "cut": {
                    copyToClipboard();
                    if (selection.length() > 0) text.delete();
                    updateText();
                }break;
            }
        });

        menu.add(this.menu);
        return true;
    }

    @Override
    protected void onCharEvent(CharEvent charEvent) {
        super.onCharEvent(charEvent);
        if (selection.length() > 0){
            text.delete();
        }
        text.insert(String.valueOf(charEvent.getCharacter()));
        updateText();
    }

    private void copyToClipboard() {
        if (selection.length() != 0) {
            CharSequence sub = text.subSequence(selection.low(), selection.high());
            ((ClipboardApplet)getContext().getApplet(Context.CLIPBOARD_APPLET)).setContent(""+sub);
        }
        updateText();
    }

    private void pasteFromClipboard() {
        String content = ((ClipboardApplet)getContext().getApplet(Context.CLIPBOARD_APPLET)).getContent();
        if(selection.length() > 0){
            text.delete();
        }
        text.insert(content);
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
                case KeyEvent.KEY_C:
                    if (keyEvent.hasCtrl()) copyToClipboard();
                    break;
                case KeyEvent.KEY_X:
                    if (keyEvent.hasCtrl()) {
                        copyToClipboard();
                        if (text.length() > 0)
                            text.delete();
                    }
                    break;
                case KeyEvent.KEY_V:
                    if (keyEvent.hasCtrl()) {
                        pasteFromClipboard();
                    }
                    break;
                case KeyEvent.KEY_A:
                    if (keyEvent.hasCtrl()){
                        selection.set(0, text.length());
                        invalidate();
                    }
                    break;
                case KeyEvent.KEY_RIGHT:
                    if (keyEvent.hasShift()) {
                        selection.set(selection.start(), selection.end()+1);
                    } else {
                        selection.set(selection.low()+1);
                    }
                    invalidate();
                    break;
                case KeyEvent.KEY_LEFT:
                    if (keyEvent.hasShift()) {
                        selection.set(selection.start(), selection.end()-1);
                    } else {
                        selection.set(selection.low()-1);
                    }
                    invalidate();
                    break;
                case KeyEvent.KEY_BACKSPACE:
                    if (selection.low() > 0 || selection.length() > 1) {
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

        if (singleLine)
            text.removeBreaks();

        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(getPaddingLeft(), getPaddingTop());
        /*if (!loopState){
            caretLoop();
        }
        FontMetrics metrics = paint.getFontMetrics();
        int lineHeight = (int) metrics.getLineHeight();
        int y = 0;
        if (selection.length() == 0) {
            drawCaret(canvas);
        } else {
            drawSelection(canvas);
        }
        paint.setColor(textColor);
        for (int i = 0; i < text.getLineCount(); i++) {
            drawLine(canvas, 0, y, i);
            y += lineHeight;
        }*/
        layout.measure(getWidth()-getPaddingLeft()-getPaddingRight(), getHeight());
        layout.draw(canvas);
        canvas.translate(-getPaddingLeft(), -getPaddingTop());
    }

    private void drawSelection(Canvas canvas) {
        int ls = text.getLineIndex(selection.low());
        int le = text.getLineIndex(selection.high());
        paint.setColor(selectionColor);
        if (ls == le){
            drawSelection(canvas, selection.low(), selection.high(),Math.round(ls * font().getLineHeight()));
        } else {
            int y = Math.round(font().getLineHeight()*ls);
            drawSelection(
                    canvas,
                    selection.low(),
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
                    selection.high(),
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

        int line = text.getLineIndex(selection.low()-1);
        int start = text.getLineStart(line);
        while (start < selection.low()) {
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