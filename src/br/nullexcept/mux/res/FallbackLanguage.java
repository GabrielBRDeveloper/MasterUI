package br.nullexcept.mux.res;

import br.nullexcept.mux.lang.xml.XmlElement;

import java.util.HashMap;

class FallbackLanguage {
    private HashMap<String, String> values = new HashMap<>();

    public String getString(String id) {
        return values.getOrDefault(id, id);
    }

    public void merge(XmlElement texts) {
        for (XmlElement child: texts.children()) {
            values.put(child.attr("name"), child.value());
        }
    }
}
