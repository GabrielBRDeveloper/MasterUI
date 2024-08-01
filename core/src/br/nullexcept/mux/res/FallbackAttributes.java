package br.nullexcept.mux.res;

import br.nullexcept.mux.graphics.ColorStateList;
import br.nullexcept.mux.graphics.Drawable;
import br.nullexcept.mux.graphics.fonts.Typeface;
import br.nullexcept.mux.lang.Function;
import br.nullexcept.mux.lang.xml.XmlElement;
import br.nullexcept.mux.utils.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class FallbackAttributes implements AttributeList {
    private final Resources resources;
    private final Map<String, String> map;
    private final ArrayList<AttributeList> fallbacks;

    public FallbackAttributes(Map<String, String> attrs, List<AttributeList> fallbacks, Resources res){
        this.resources = res;
        this.map = attrs;
        this.fallbacks = new ArrayList<>();
        for (AttributeList attr: fallbacks) {
            if (attr == null) continue;
            this.fallbacks.add(attr);
        }
    }

    public FallbackAttributes(XmlElement xml, List<AttributeList> fallbacks, Resources res){
        this(xml.attrs(), fallbacks, res);
    }

    private String resolve(String value){
        if (value != null && value.startsWith("?")){
            String name = value.substring(1);
            if (map.containsKey(name)){
                return resolve(map.get(name));
            }
            String[] resolved = new String[1];
            for (AttributeList fallback: fallbacks) {
                try {
                    fallback.searchRaw(name, v -> resolved[0] = v);
                    if (resolved[0] != null) {
                        return resolved[0];
                    }
                } catch (Exception e){
                    // Don't do anything because call undefined resolve from other FallbackAttributes
                }
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
    public void searchColorList(String name, Function<ColorStateList> apply) {
        ColorStateList color = getColorList(name);
        if (color != null) {
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
        } else {
          String[] data = new String[1];
          for (AttributeList fallback : fallbacks) {
              fallback.searchRaw(name, value -> data[0] = value);
              if (data[0] != null) {
                  apply.call(data[0]);
                  break;
              }
          }
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
        return (map.get(name));
    }

    @Override
    public CharSequence getText(String name) {
        if (contains(name)){
            return Parser.parseText(resources, resolve(map.get(name)));
        } else {
            for (AttributeList fallback : fallbacks) {
                CharSequence seq = fallback.getText(name);
                if (seq != null) {
                    return seq;
                }
            }
        }
        return null;
    }

    @Override
    public int getColor(String name, int defaultValue) {
        if (contains(name)){
            return Parser.parseColor(resolve(map.get(name)));
        }  else {
            for (AttributeList fallback : fallbacks) {
                int color = fallback.getColor(name, Integer.MAX_VALUE);
                if (color != Integer.MAX_VALUE) {
                    return color;
                }
            }
        }
        return defaultValue;
    }

    @Override
    public float getDimension(String name, float defaultValue) {
        if (contains(name)){
            return Parser.parseDimension(resources, resolve(map.get(name)));
        } else {
            for (AttributeList fallback: fallbacks) {
                float dimension = fallback.getDimension(name, Float.MAX_VALUE);
                if (dimension != Float.MAX_VALUE) {
                    return dimension;
                }
            }
        }
        return defaultValue;
    }

    @Override
    public Drawable getDrawable(String name) {
        if (contains(name)){
            return Parser.parseDrawable(resources, resolve(map.get(name)));
        } else {
            for (AttributeList fallback : fallbacks) {
                Drawable drawable = fallback.getDrawable(name);
                if (drawable != null) {
                    return drawable;
                }
            }
        }
        return null;
    }

    @Override
    public ColorStateList getColorList(String name) {
        if (contains(name)) {
            return Parser.parseColorState(resources, resolve(map.get(name)));
        } else {
            for (AttributeList fallback: fallbacks) {
                ColorStateList color = fallback.getColorList(name);
                if (color != null) {
                    return fallback.getColorList(name);
                }
            }
        }
        return null;
    }

    @Override
    public boolean contains(String name) {
        return map.containsKey(name);
    }
}
