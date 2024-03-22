package br.nullexcept.mux.widget;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.input.MotionEvent;
import br.nullexcept.mux.res.AttributeList;
import br.nullexcept.mux.view.Gravity;
import br.nullexcept.mux.view.PointerIcon;

public class Button extends TextView {
    public Button(Context context) {
        super(context);
    }

    public Button(Context context, AttributeList attrs) {
        super(context, attrs);
        setGravity(Gravity.CENTER);
        setPointerIcon(new PointerIcon(PointerIcon.Model.HAND));
    }

    @Override
    protected void onMouseMoved(MotionEvent event) {
        super.onMouseMoved(event);
    }

    @Override
    protected void onInflate(AttributeList attr) {
        super.onInflate(attr);
    }
}
