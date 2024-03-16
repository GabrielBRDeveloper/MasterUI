package br.nullexcept.mux.res;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.graphics.Drawable;
import br.nullexcept.mux.lang.Function;
import br.nullexcept.mux.lang.xml.XmlElement;

class AttributeAgent implements AttributeList {
    private final Context context;
    private final Resources resources;
    private final XmlElement xml;
    private final AttributeList fallback;

    public AttributeAgent(XmlElement element, AttributeList fallback, Context context){
        this.context = context;
        this.resources = context.getResources();
        this.xml = element;
        this.fallback = fallback;
    }

    private String resolve(String value){
        if (value.startsWith("?")){

        }
        return value;
    }

    @Override
    public void searchText(String name, Function<CharSequence> apply) {
        CharSequence value;
        if ((value = getText(name)) != null){
            apply.call(value);
        }
    }

    @Override
    public void searchColor(String name, Function<Integer> apply) {
        int color = getColor(name, -1);
        if (color != -1){
            apply.call(color);
        }
    }

    @Override
    public void searchDimension(String name, Function<Float> apply) {
        float dimen = getDimension(name, -1.0f);
        if (dimen != -1){
            apply.call(dimen);
        }
    }

    @Override
    public void searchDrawable(String name, Function<Drawable> apply) {
        Drawable drawable = getDrawable(name);
        if (drawable != null){
            apply.call(drawable);
        }
    }

    @Override
    public void searchFloat(String name, Function<Float> apply) {
        searchRaw(name, value -> apply.call(Parser.parseFloat(value)));
    }

    @Override
    public void searchRaw(String name, Function<String> apply) {
        if (contains(name)){
            apply.call(resolve(xml.attr(name)));
        } else if (fallback != null){
            fallback.searchRaw(name, apply);
        }
    }

    @Override
    public void searchBoolean(String name, Function<Boolean> apply) {
        searchRaw(name, value -> apply.call(Parser.parseBoolean(value)));
    }

    @Override
    public String[] names() {
        return xml.attrNames();
    }

    @Override
    public String getRawValue(String name) {
        return xml.attr(name);
    }

    @Override
    public CharSequence getText(String name) {
        if (contains(name)){
            return resolve(xml.attr(name));
        } else if (fallback != null){
            return fallback.getText(name);
        }
        return null;
    }

    @Override
    public int getColor(String name, int defaultValue) {
        if (contains(name)){
            return Parser.parseColor(resolve(xml.attr(name)));
        }  else if (fallback != null){
            return fallback.getColor(name, defaultValue);
        }
        return defaultValue;
    }

    @Override
    public float getDimension(String name, float defaultValue) {
        if (contains(name)){
            return Parser.parseDimension(resources, resolve(xml.attr(name)));
        } else if (fallback != null){
            return fallback.getDimension(name, defaultValue);
        }
        return defaultValue;
    }

    @Override
    public Drawable getDrawable(String name) {
        if (contains(name)){
            return Parser.parseDrawable(resources, resolve(xml.attr(name)));
        } else if (fallback != null){
            return fallback.getDrawable(name);
        }
        return null;
    }

    @Override
    public boolean contains(String name) {
        return xml.has(name);
    }
}
