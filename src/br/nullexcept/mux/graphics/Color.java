package br.nullexcept.mux.graphics;

public class Color {

    public static final int RED = Color.rgb(255,0,0);
    public static final int GREEN = Color.rgb(0,255,0);
    public static final int BLUE = Color.rgb(0,0,255);
    public static final int BLACK = Color.rgb(0,0,0);
    public static final int WHITE = Color.rgb(255,255,255);

    public static int red(int color){
        return color >> 16 & 255;
    }

    public static int green(int color){
        return color >> 8 & 255;
    }

    public static int blue(int color){
        return color & 255;
    }

    public static int alpha(int color){
        return color >> 24 & 255;
    }

    public static int rgb(int red, int green, int blue){
        return argb(255,red,green,blue);
    }

    public static int argb(int alpha, int red, int green, int blue){
        return (alpha & 255) << 24 | (red & 255) << 16 | (green & 255) << 8 | (blue & 255);
    }
}
