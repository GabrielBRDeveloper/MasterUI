package br.nullexcept.mux.res;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.graphics.fonts.Typeface;
import br.nullexcept.mux.graphics.fonts.TypefaceFactory;
import br.nullexcept.mux.lang.xml.XmlElement;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

import java.util.HashMap;


/**
 * Stylesheet for views:
 * CustomView -> ClassParent -> ClassParent... -> Theme
 * Stylesheet basics:
 * Stylesheet -> parent -> parent -> theme
 */


public final class Resources {
    private DisplayMetrics metrics;
    private static final HashMap<String, XmlElement> cache = new HashMap<>();
    private final HashMap<String, StylePreset> styles = new HashMap<>();

    private final LayoutInflater inflater;
    private final Context context;
    private final AssetsManager assetsManager;
    private FallbackAttributes theme;

    static final AssetsManager Manager;

    static {
        Manager = new AssetsManager("/res/", Resources.class);
        Typeface.DEFAULT = TypefaceFactory.create(Manager.openDocument("fonts/Roboto/Roboto-Regular.ttf"));
    }

    public Resources(Context ctx){
        this.context = ctx;
        metrics = new DisplayMetrics();
        inflater = new LayoutInflater(ctx);
        assetsManager = new AssetsManager("/assets/", ctx.getClass());
        importStylesheet(requestXml("style/defaults"));
        setTheme("Base.Theme");
    }

    public AssetsManager getAssetsManager() {
        return assetsManager;
    }

    private void importStylesheet(XmlElement xml){
        for (int i = 0; i < xml.childCount(); i++){
            StylePreset style = new StylePreset(this, xml.childAt(i));
            if (styles.containsKey(style.getName())){
                throw new RuntimeException("Style name duplicate: "+style.getName());
            }
            styles.put(style.getName(), style);
        }
    }

    public AttributeList obtainStyled(XmlElement xml){
        return new FallbackAttributes(xml, theme, this);
    }

    public AttributeList obtainStyled(String name){
        if (styles.containsKey(name)){
            return styles.get(name).generate(theme);
        }
        return theme;
    }

    public void setTheme(String styleId){
        theme = styles.get(styleId).generate(null);
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public DisplayMetrics getDisplayMetrics() {
        return metrics;
    }

    XmlElement requestXml(String path){
        if (cache.containsKey(path)){
            return cache.get(path);
        }
        cache.put(path, XmlElement.parse(Resources.Manager.openDocument(path+".xml")));
        return requestXml(path);
    }

    StylePreset obtainStyle(String id) {
        return styles.get(id);
    }

    public Context getContext() {
        return context;
    }

    public static class DisplayMetrics {
        public final double pm;
        public final double pt;
        public final double cm;
        public final double sp;
        public final double px = 1.0;

        public DisplayMetrics() {
            long monitor = GLFW.glfwGetPrimaryMonitor();
            GLFWVidMode mode =GLFW.glfwGetVideoMode(monitor);
            int dw = mode.width();
            {
                int[][] sizes = new int[2][1];
                GLFW.glfwGetMonitorPhysicalSize(monitor, sizes[0], sizes[1]);
                pm = (double) dw / sizes[0][0];
                pt = (pm * 0.35277777777778);
                cm = pm * 10;
            }
            {
                float[] scale = new float[1];
                GLFW.glfwGetMonitorContentScale(monitor,scale,scale);
                sp = scale[0];
            }
        }
    }
}
