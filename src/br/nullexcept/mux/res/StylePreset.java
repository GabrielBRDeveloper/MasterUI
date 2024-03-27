package br.nullexcept.mux.res;

import br.nullexcept.mux.lang.xml.XmlElement;

import java.util.HashMap;
import java.util.Map;

class StylePreset {
    private final String name;
    private final Resources resources;
    private final Map<String, String> presets;
    private final String parent;
    public StylePreset(Resources res, XmlElement element){
        this.name = element.attr("name");
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < element.childCount(); i++){
            XmlElement node = element.childAt(i);
            map.put(node.attr("name"), node.value());
        }
        this.parent = element.has("parent") ? element.attr("parent") : null;
        this.resources = res;
        this.presets = map;
    }

    public String getName() {
        return name;
    }

    public FallbackAttributes generate(FallbackAttributes fallback){
        if (parent != null){
            fallback = resources.obtainStyle(parent).generate(fallback);
        }
        return new FallbackAttributes(presets,fallback,resources);
    }
}
