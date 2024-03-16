package br.nullexcept.mux.widget;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.input.MotionEvent;
import br.nullexcept.mux.res.AttributeList;
import br.nullexcept.mux.view.Gravity;

public class Button extends TextView {
    public Button(Context context) {
        super(context);
    }

    public Button(Context context, AttributeList attrs) {
        super(context, attrs);
    }

    {
        setGravity(Gravity.CENTER);
    }

    @Override
    protected void onMouseMoved(MotionEvent event) {
        super.onMouseMoved(event);
    }

    @Override
    protected void onRequestAttribute(AttributeList attr) {
        super.onRequestAttribute(attr);
    }
}
