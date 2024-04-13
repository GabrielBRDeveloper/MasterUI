package br.nullexcept.mux.text;

import br.nullexcept.mux.graphics.Canvas;
import br.nullexcept.mux.graphics.Paint;
import br.nullexcept.mux.graphics.Size;
import br.nullexcept.mux.graphics.fonts.FontMetrics;
import br.nullexcept.mux.view.Gravity;

public class TextLayout {
    private final Editable text;
    private final FontMetrics font;
    private final Size viewport = new Size();
    private final Selection selection;
    private final TextRenderer drawer;

    private int gravity;
    // [INTERNAL LINE] => LINE | START | END | WIDTH
    private int breakLines = 0;
    private Size wrapSize = new Size();
    private final int[][] lines = new int[10240][4]; //160KB of buffer allow 10240 lines

    public TextLayout(Editable text, Paint paint, TextRenderer drawer) {
        this.text = text;
        this.selection = text.getSelection();
        this.font = paint.getFontMetrics();
        this.drawer = drawer;
    }

    public int getWidth() {
        return viewport.width;
    }

    public int getHeight() {
        return viewport.height;
    }

    /* NEED MAKE A WORD BREAKS */
    public void measure(int width, int height, int gravity) {
        this.gravity = gravity;
        width = Math.max(1, width);
        height = Math.max(1, height);

        viewport.set(width, height);
        wrapSize.set(0,0);

        int li = 0;
        if (text.length() > 0) {
            for (int line = 0; line < text.getLineCount(); line++) {
                li += measureLine(li, text.getLineStart(line), text.getLineEnd(line), line);
            }
        }
        wrapSize.height = (int) (Math.max(1,li) * font.getLineHeight());
        breakLines = li;
    }

    public int getWrapLineCount() {
        return breakLines;
    }

    private int measureLine(int index, int start, int end, int line) {
        int width = measureRange(start, end);
        int lineCount = 1;

        if (width > viewport.width) {
            int space = text.indexOf(" ", start+1);
            width = space == -1 ? width : measureRange(start, space);
            if (space != -1 && width < viewport.height) {
                int newSpace = width;
                int missingSpace = 0;
                int spaceIndex = space;
                while (newSpace < viewport.width) {
                    space = spaceIndex;
                    int nsp = text.indexOf(" ", spaceIndex+1);
                    if (nsp == -1) {
                        missingSpace = 0;
                        break;
                    }
                    newSpace += (missingSpace = measureRange(space, nsp));
                    spaceIndex = nsp;

                }
                width = newSpace - missingSpace;
            }

            if (width > viewport.width) {
                int x = (int) Math.round((((end - start) / (double) width) * viewport.width) + 8);
                x = Math.max(0, x);
                x = Math.min(end - start, x);
                int e = Math.min(start + x, end);
                width = measureRange(start, e);
                while (width > viewport.width && e > start) {
                    width -= font.measureChar(text.charAt(e & (text.length() - 1)));
                    e--;
                }

                if (e != start) {
                    lineCount += measureLine(index + 1, e, end, line);
                }
                end = e;
                width = measureRange(start, end);
            } else {
                if (space != start && space != end) {
                    lineCount += measureLine(index+1, space+1, end, line);
                }
                end = space+1;
            }
        }

        lines[index][0] = line;
        lines[index][1] = start;
        lines[index][2] = end;
        lines[index][3] = width;

        wrapSize.width = Math.max(lines[index][3], wrapSize.width);
        return lineCount;
    }

    private int findInternalLine(int index) {
        int line = text.getLineIndex(index);
        int c = Math.max(0, line-1);
        for (;c < breakLines; c++) {
            if (lines[c][0] == line && lines[c][2] >= index)
                return c;
        }
        return Math.max(0, Math.min(c, breakLines-1));
    }

    private int measureRange(int start, int end) {
        int w = 0;
        for (int i = start; i < end; i++) {
            w += font.measureChar(text.charAt(i));
        }
        return w;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
        measure(viewport.width, viewport.height, gravity);
    }

    public void update() {
        measure(viewport.width, viewport.height, gravity);
    }

    public void draw(Canvas canvas, int width, int height, boolean selected) {
        int ox = viewport.width;
        int oy = viewport.height;
        viewport.set(width, height);
        int y = Gravity.apply(Gravity.vertical(gravity), viewport.height, Math.round(breakLines * font.getLineHeight()));

        if (selected) {
            if (selection.length() > 0) {
                drawSelection(canvas);
            } else {
                drawCaret(canvas);
            }
        }

        for (int i = 0; i < breakLines; i++) {
            drawLine(canvas,y + Math.round(i*font.getLineHeight()),i);
        }
        viewport.set(ox, oy);
    }

    private void drawCaret(Canvas canvas) {
        int l = selection.low();
        int line = findInternalLine(selection.low());
        if (selection.low() == lines[line][1] && text.length() > 0 && line > 0) {
            if (text.charAt(l) == '\n') {
                line--;
            }
        }
        int x = 0;
        for (int i = lines[line][1]; i < lines[line][2] && i != l; i++) {
            x += font.measureChar(text.charAt(i));
        }

        drawer.drawCaret(canvas,x, (int) (line*font.getLineHeight()));
    }

    private void drawSelection(Canvas canvas) {
        int startLine = findInternalLine(selection.low());
        int endLine = findInternalLine(selection.high());

        if (startLine == endLine) {
            drawSelection(canvas, selection.low(), selection.end(), startLine);
        } else {
            drawSelection(canvas, selection.low(), lines[startLine][2], startLine);
            for (int i = startLine + 1; i < endLine; i++) {
                drawSelection(canvas, lines[i][1], lines[i][2], i);
            }
            drawSelection(canvas, lines[endLine][1], selection.end(), endLine);
        }
    }

    private void drawSelection(Canvas canvas, int start, int end, int line) {
        int z = Gravity.apply(Gravity.vertical(gravity), viewport.height, Math.round(breakLines * font.getLineHeight()));
        int w;

        if (start == lines[line][1] && end == lines[line][2]) {
            w = lines[line][3];
        } else {
            w = measureRange(start, end);
        }

        int y = (int) (line * font.getLineHeight()) + z;
        int x = Gravity.apply(Gravity.horizontal(gravity), viewport.width, w);
        drawer.drawSelection(canvas, x,y, w, Math.round(font.getLineHeight()));
    }

    public void drawLine(Canvas canvas, int y, int index) {
        y += font.getAscent();
        int[] line = lines[index];
        int x = Gravity.apply(Gravity.horizontal(gravity),viewport.width,line[3]);

        for (int i = line[1]; i < line[2]; i++) {
            char ch = text.charAt(i);
            drawer.drawCharacter(canvas, ch, x, y);
            x += font.measureChar(ch);
        }
    }

    public Size getWrapSize() {
        return wrapSize;
    }

    public int getGravity() {
        return gravity;
    }

    public interface TextRenderer {
        void drawSelection(Canvas canvas, int x, int y, int width, int height);
        void drawCharacter(Canvas canvas, char ch, int x, int y);
        void drawCaret(Canvas canvas, int x, int y);
    }
}