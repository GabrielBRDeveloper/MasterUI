package br.nullexcept.mux.text;

public class Editable implements CharSequence {
    private final StringBuilder builder = new StringBuilder();
    private int lineCount = 0;
    private final int[][] lines = new int[4096][2];
    private final TextSelection selection = new TextSelection(this);
    private TextObserver observer = (text)->{};

    public Editable() {
        measure();
    }

    private void measure() {
        lineCount = 0;
        int index = -1;
        int start = 0;

        if (builder.length() == 0) {
            lineCount = 1;
            lines[0][0] = 0;
            lines[0][1] = 0;
            selection.update();
            return;
        }

        while ((index = builder.indexOf("\n", ++index)) >= 0) {
            lines[lineCount][0] = start;
            lines[lineCount][1] = index;
            lineCount++;
            start = index;
        }

        if (start < builder.length()) {
            lines[lineCount][0] = start;
            lines[lineCount][1] = builder.length();
            lineCount++;
        }
        selection.update();
    }

    public void setObserver(TextObserver observer) {
        this.observer = observer;
    }

    public int getLineLength(int line) {
        return getLineEnd(line) - getLineStart(line);
    }

    public int getLineCount() {
        return lineCount;
    }

    public int getLineIndex(int index) {
        if (index >= builder.length())
            return lineCount - 1;
        int i = lineCount - 1;
        while (i >= 0) {
            if (index >= lines[i][0]) {
                return i;
            }
            i--;
        }
        return 0;
    }


    public void insert(CharSequence text) {
        insert(selection.low(), text);
        selection.set(selection.low()+text.length());
        selection.update();
    }

    public TextSelection getSelection() {
        return selection;
    }

    public void delete() {
        int start = selection.low();
        int end = selection.high();
        if (start == end) {
            if (start > 0) {
                delete(start - 1, 1);
                selection.set(start - 1);
            }
        } else {
            delete(start, end - start);
            selection.set(start);
        }
    }

    public int getLineStart(int line) {
        return lines[line][0];
    }

    public int getLineEnd(int line) {
        return lines[line][1];
    }

    public void insert(int index, CharSequence text) {
        index = Math.max(0, Math.min(builder.length(), index));
        builder.insert(index, text);
        measure();
        observer.onChange(this);
    }

    public void delete(int index, int length) {
        builder.delete(index, index + length);
        measure();
        observer.onChange(this);
    }

    public int indexOf(String text) {
        return builder.indexOf(text);
    }

    public int indexOf(String text, int last) {
        return builder.indexOf(text, last);
    }

    @Override
    public int length() {
        return builder.length();
    }

    @Override
    public char charAt(int index) {
        return builder.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        if (start == end || start > end)
            return "";
        return builder.subSequence(start, end);
    }

    public void removeBreaks() {
        int index;
        while ((index = builder.indexOf("\n")) != -1) {
            builder.delete(index, index + 1);
        }
        measure();
        observer.onChange(this);
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}