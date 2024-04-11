package br.nullexcept.mux.res;

import br.nullexcept.mux.graphics.Drawable;
import br.nullexcept.mux.lang.Function;
import br.nullexcept.mux.lang.xml.XmlElement;

import java.util.Map;

class FallbackAttributes implements AttributeList {
    private final Resources resources;
    private final Map<String, String> map;
    private final FallbackAttributes fallback;

    public FallbackAttributes(Map<String, String> attrs, FallbackAttributes fallback, Resources res){
        this.resources = res;
        this.map = attrs;
        this.fallback = fallback;
    }

    public FallbackAttributes(XmlElement xml, FallbackAttributes fallback, Resources res){
        this(xml.attrs(), fallback, res);
    }

    private String resolve(String value){
        if (value.startsWith("?")){
            String name = value.substring(1);
            if (map.containsKey(name)){
                return resolve(map.get(name));
            }
            if (fallback != null){
                return fallback.resolve(value);
            }
            throw new RuntimeException("Cannot resolve reference: "+name);
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
        int color = getColor(name, Integer.MIN_VALUE);
        if (color != Integer.MIN_VALUE){
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
            apply.call(resolve(map.get(name)));
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
        return map.keySet().toArray(new String[0]);
    }

    @Override
    public String getRawValue(String name) {
        return map.get(name);
    }

    @Override
    public CharSequence getText(String name) {
        if (contains(name)){
            return Parser.parseText(resources, resolve(map.get(name)));
        } else if (fallback != null){
            return fallback.getText(name);
        }
        return null;
    }

    @Override
    public int getColor(String name, int defaultValue) {
        if (contains(name)){
            return Parser.parseColor(resolve(map.get(name)));
        }  else if (fallback != null){
            return fallback.getColor(name, defaultValue);
        }
        return defaultValue;
    }

    @Override
    public float getDimension(String name, float defaultValue) {
        if (contains(name)){
            return Parser.parseDimension(resources, resolve(map.get(name)));
        } else if (fallback != null){
            return fallback.getDimension(name, defaultValue);
        }
        return defaultValue;
    }

    @Override
    public Drawable getDrawable(String name) {
        if (contains(name)){
            return Parser.parseDrawable(resources, resolve(map.get(name)));
        } else if (fallback != null){
            return fallback.getDrawable(name);
        }
        return null;
    }

    @Override
    public boolean contains(String name) {
        return map.containsKey(name);
    }
}
