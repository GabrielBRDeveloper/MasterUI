package br.nullexcept.mux.widget;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.res.AttributeList;

public class Button extends TextView {
    public Button(Context context) {
        super(context);
    }

    public Button(Context context, AttributeList attrs) {
        super(context, attrs);
    }

    {
        setOnClickListener(v -> {});
    }
}
