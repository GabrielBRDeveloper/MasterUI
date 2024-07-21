package br.nullexcept.mux.widget;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.graphics.StateList;
import br.nullexcept.mux.lang.Function2;
import br.nullexcept.mux.res.AttributeList;
import br.nullexcept.mux.view.AttrList;
import br.nullexcept.mux.view.View;

public class Switch extends View {
    private boolean checked;
    private Function2<View, Boolean> checkedListener;

    public Switch(Context context) {
        this(context, null);
    }

    public Switch(Context context, AttributeList attrs) {
        super(context, attrs);
        attrs = initialAttributes();
        attrs.searchBoolean(AttrList.checked, this::setChecked);
    }

    {
        setOnClickListener( v -> setChecked(!checked));
    }

    public void setChecked(boolean checked){
        this.checked = checked;
        getStateList().set(StateList.CHECKED, checked);
        changeDrawableState();
        if (checkedListener != null) {
            checkedListener.run(this, checked);
        }
    }

    public void setOnCheckedListener(Function2<View, Boolean> listener){
        this.checkedListener = listener;
        setChecked(checked);
    }
}
