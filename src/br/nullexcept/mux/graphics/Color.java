package br.nullexcept.mux.graphics;

public class Color {

    public static final int RED = Color.rgb(255,0,0);
    public static final int GREEN = Color.rgb(0,255,0);
    public static final int BLUE = Color.rgb(0,0,255);
    public static final int BLACK = Color.rgb(0,0,0);
    public static final int WHITE = Color.rgb(255,255,255);
    public static final int TRANSPARENT = Color.argb(0,0,0,0);

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

    public static int parseColor(String color) {
        if (color.startsWith("#")){
            color = color.substring(1);
        }
        if (color.length() == 3){
            color = ""+
                    color.charAt(0)+color.charAt(0)+ // RED
                    color.charAt(1)+color.charAt(1)+ // GREEN
                    color.charAt(2)+color.charAt(2); // BLUE
            return parseColor(color);
        } else if (color.length() == 4){
            return parseColor(""+
                    color.charAt(0)+color.charAt(0)+ // ALPHA
                    color.charAt(1)+color.charAt(1)+ // RED
                    color.charAt(2)+color.charAt(2)+ // GREEN
                    color.charAt(3)+color.charAt(3));// BLUE
        }

        if (color.length() == 6){
            color = "FF"+color;
        }
        return (int) Long.parseLong(color, 16);
    }
}
