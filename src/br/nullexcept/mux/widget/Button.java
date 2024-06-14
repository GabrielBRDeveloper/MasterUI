package br.nullexcept.mux.widget;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.input.KeyEvent;
import br.nullexcept.mux.res.AttributeList;
import br.nullexcept.mux.utils.Log;

public class Button extends TextView {
    private OnClickListener listener;
    public Button(Context context) {
        super(context);
    }

    public Button(Context context, AttributeList attrs) {
        super(context, attrs);
    }

    {
        setFocusable(true);
        super.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(this);
            }
            releaseFocus();
        });
    }

    @Override
    public void setOnClickListener(OnClickListener clickListener) {
        listener = clickListener;
    }

    @Override
    protected void onKeyEvent(KeyEvent keyEvent) {
        super.onKeyEvent(keyEvent);
        if (keyEvent.getKeyCode() == KeyEvent.KEY_ENTER && isFocused()) {
            if(listener != null) {
                listener.onClick(this);
            }
            releaseFocus();
        }
    }
}
