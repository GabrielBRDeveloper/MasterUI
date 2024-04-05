package br.nullexcept.mux.graphics.drawable;

import br.nullexcept.mux.graphics.Canvas;
import br.nullexcept.mux.graphics.Drawable;
import br.nullexcept.mux.graphics.Rect;
import br.nullexcept.mux.graphics.fonts.Typeface;
import br.nullexcept.mux.graphics.fonts.TypefaceFactory;
import br.nullexcept.mux.utils.BufferUtils;
import br.nullexcept.mux.view.Gravity;

import java.util.HashMap;

public class MaterialIconDrawable extends Drawable {
    private static boolean INITIALIZED = false;
    private static Typeface font;
    private static final HashMap<String, Character> codePoints = new HashMap<>();
    private String icon = "error";

    public MaterialIconDrawable() {
        initialize();
        paint.setTypeface(font);
    }

    public MaterialIconDrawable(String icon) {
        this();
        setIcon(icon);
    }

    public MaterialIconDrawable(String icon, int color) {
        this(icon);
        setColor(color);
    }

    public void setColor(int color) {
        paint.setColor(color);
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public void draw(Canvas canvas) {
        if (codePoints.containsKey(icon)) {
            Rect bounds = getBounds();
            int s = Math.min(bounds.width(), bounds.height());
            paint.setTextSize(s);
            while (paint.getFontMetrics().getLineHeight() >= s) {
                paint.setTextSize(s - 1);
            }

            Rect rect = new Rect();
            int h = Math.round(paint.getFontMetrics().getLineHeight());
            Gravity.applyGravity(Gravity.CENTER, h, h, bounds.width(), bounds.height(), rect);
            String ic = codePoints.get(icon).toString();
            canvas.drawText(ic, rect.left+bounds.left, rect.top + h +bounds.top, paint);
        }
    }

    @Override
    public int getWidth() {
        return 24;
    }

    @Override
    public int getHeight() {
        return 24;
    }

    protected static void initialize() {
        if (INITIALIZED)return;
        INITIALIZED = true;

        font = TypefaceFactory.create(MaterialIconDrawable.class.getResourceAsStream("/res/fonts/MaterialIcons/MaterialIcons-Regular.ttf"));
        String[] points = BufferUtils.utfFromStream(MaterialIconDrawable.class.getResourceAsStream("/res/fonts/MaterialIcons/icons.codepoints")).split("\n");
        for (String point: points) {
            String[] value = point.split(" ");
            codePoints.put(value[0], (char) Integer.parseInt(value[1].toUpperCase(), 16));
        }
    }
}
