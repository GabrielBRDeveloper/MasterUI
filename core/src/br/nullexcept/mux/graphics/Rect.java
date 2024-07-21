package br.nullexcept.mux.graphics;

public class Rect {
    public int left;
    public int top;
    public int right;
    public int bottom;

    public Rect(){
        this(0,0,0,0);
    }

    public Rect(int left, int top, int right, int bottom){
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public void set(int left, int top, int right, int bottom){
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public int width(){
        return right - left;
    }

    public int height(){
        return bottom - top;
    }

    public void zero(){
        set(0,0,0,0);
    }


    public void move(int x, int y){
        left += x;
        top += y;
        right += x;
        bottom += y;
    }

    public void setPosition(int x, int y){
        int w = width();
        int h = height();
        left = x;
        top = y;
        setSize(w,h);
    }

    public void setSize(int width, int height){
        right = left + width;
        bottom = top + height;
    }

    public void set(Rect rect) {
        set(rect.left, rect.top, rect.right, rect.bottom);
    }

    @Override
    public String toString() {
        return "Rect{" +
                "left=" + left +
                ", top=" + top +
                ", right=" + right +
                ", bottom=" + bottom +
                '}';
    }

    public boolean inner(float x, float y) {
        return x >= left && y >= top && x <= right&& y <= bottom;
    }
}