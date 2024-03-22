package br.nullexcept.mux.res;

import br.nullexcept.mux.graphics.Color;
import br.nullexcept.mux.graphics.Drawable;
import br.nullexcept.mux.graphics.drawable.ColorDrawable;
import br.nullexcept.mux.lang.Log;

class Parser {
    public static float parseFloat(String value){
        return Float.parseFloat(value);
    }

    public static boolean parseBoolean(String value){
        return Boolean.parseBoolean(value);
    }

    public static float parseDimension(Resources res, String dimen) {
        Resources.DisplayMetrics dm = res.getDisplayMetrics();
        String type = dimen.substring(dimen.length() - 2).toLowerCase();
        float size = Float.parseFloat(dimen.substring(0, dimen.length() - 2));
        switch (type) {
            case "mm":
                return (float) (dm.pm * size);
            case "cm":
                return (float) (dm.cm * size);
            case "pt":
                return (float) (dm.pt * size);
            case "dp":
            case "sp":
                return (float) (dm.sp * size);
            case "px":
                return (float) (size * dm.px);
        }
        throw new IllegalArgumentException("Invalid "+type);
    }

    public static int parseColor(String color) {
        return Color.RED;
    }

    public static Drawable parseDrawable(Resources resources, String value) {
        if (value.startsWith("#")){
            return new ColorDrawable(Color.parseColor(value));
        } else {
            Log.log("ResourceParser","Invalid background value: "+value);
        }
        return null;
    }
}