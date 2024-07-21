package br.nullexcept.mux.res;

import br.nullexcept.mux.graphics.fonts.Typeface;
import br.nullexcept.mux.lang.xml.XmlElement;

import java.util.HashMap;

class ResourceCache {
    private static final HashMap<String, XmlElement> xmlCache = new HashMap<>();
    private static final HashMap<String, Typeface> fontCache = new HashMap<>();

    public static XmlElement obtainXml(String path) {
        return xmlCache.get(path);
    }

    public static boolean hasXml(String path) {
        return xmlCache.containsKey(path);
    }

    public static Typeface obtainTypeface(String name) {
        return fontCache.get(name);
    }

    public static boolean hasFont(String path) {
        return fontCache.containsKey(path);
    }

    public static void store(String path, Typeface typeface) {
        fontCache.put(path, typeface);
    }

    public static void store(String path, XmlElement xml) {
        xmlCache.put(path, xml);
    }
}
