package br.nullexcept.mux.widget;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.graphics.Canvas;
import br.nullexcept.mux.graphics.shape.RoundedShape;
import br.nullexcept.mux.graphics.shape.ShapeList;
import br.nullexcept.mux.res.AttributeList;
import br.nullexcept.mux.view.AttrList;

public class CardLayout extends AbsoluteLayout {
    private final RoundedShape shape = new RoundedShape();
    private final ShapeList clipArea = new ShapeList(shape);

    public CardLayout(Context context) {
        this(context, null);
    }

    public CardLayout(Context context, AttributeList attrs) {
        super(context, attrs);
        initialAttributes().searchDimension(AttrList.radius,(dimen) -> shape.setRadius(dimen.intValue()));
    }

    public void setRadius(int radius) {
        shape.setRadius(radius);
        invalidate();
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);
        shape.setPosition(1,1);
        shape.resize(canvas.getWidth()-2,canvas.getHeight()-2);
        canvas.clip(clipArea);
    }
}
