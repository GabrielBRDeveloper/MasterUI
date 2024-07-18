package br.nullexcept.mux.graphics.fonts;

import br.nullexcept.mux.graphics.Paint;

public abstract class Typeface {
    public static final int STYLE_NORMAL = 0;
    public static final int STYLE_BOLD = 1;
    public static final int STYLE_ITALIC = 2;
    public static Typeface DEFAULT;

    public abstract FontMetrics getMetricsFor(Paint paint);
}
