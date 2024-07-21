package br.nullexcept.mux.text;

public class Selection {
    private int start = 0;
    private int end = 0;
    private final CharSequence text;

    public Selection(CharSequence text) {
        this.text = text;
    }

    public int start() {
        return start;
    }

    public int end() {
        return end;
    }

    public void set(int start, int end) {
        this.start = start;
        this.end = end;
        update();
    }

    public int length() {
        return high() - low();
    }

    public void update() {
        start = Math.max(0, Math.min(text.length(), start));
        end = Math.max(0, Math.min(text.length(), end));
    }

    public int low() {
        return Math.min(start, end);
    }

    public int high() {
        return Math.max(start, end);
    }

    public void set(int index) {
        set(index, index);
    }
}
