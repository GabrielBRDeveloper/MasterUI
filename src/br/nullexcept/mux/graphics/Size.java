package br.nullexcept.mux.graphics;

public class Size {
    public int width;
    public int height;

    public Size() {
        this(0,0);
    }

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Size(Size size) {
        this(size.width, size.height);
    }

    public void set(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
