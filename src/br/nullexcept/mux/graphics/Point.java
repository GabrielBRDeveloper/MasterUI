package br.nullexcept.mux.graphics;

public class Point {
    public int x;
    public int y;

    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Point(){
        this(0,0);
    }

    public void set(int x, int y){
        this.x = x;
        this.y = y;
    }
}
