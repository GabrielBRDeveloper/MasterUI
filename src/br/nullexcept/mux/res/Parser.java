package br.nullexcept.mux.res;

import br.nullexcept.mux.graphics.*;
import br.nullexcept.mux.graphics.drawable.*;
import br.nullexcept.mux.graphics.shape.OvalShape;
import br.nullexcept.mux.graphics.shape.RectShape;
import br.nullexcept.mux.graphics.shape.RoundedShape;
import br.nullexcept.mux.graphics.shape.Shape;
import br.nullexcept.mux.lang.Log;
import br.nullexcept.mux.lang.xml.XmlElement;
import br.nullexcept.mux.view.AttrList;
import org.w3c.dom.Attr;

import java.util.HashMap;

class Parser {
    private static final String LOG_TAG = "ResourcesParser";

    public static float parseFloat(String value){
        return Float.parseFloat(value);
    }

    public static boolean parseBoolean(String value){
        return Boolean.parseBoolean(value);
    }

    public static float parseDimension(Resources res, String dimen) {
        Resources.DisplayMetrics dm = res.getDisplayMetrics();
        String type = dimen.substring(dimen.length() - 2).toLowerCase();
        float size = Float.parseFloat(dimen.substring(0, dimen.length() - 2));
        switch (type) {
            case "mm":
                return (float) (dm.pm * size);
            case "cm":
                return (float) (dm.cm * size);
            case "pt":
                return (float) (dm.pt * size);
            case "dp":
            case "sp":
                return (float) (dm.sp * size);
            case "px":
                return (float) (size * dm.px);
        }
        throw new IllegalArgumentException("Invalid "+type);
    }

    public static int parseColor(String color) {
        return Color.parseColor(color);
    }

    private static Drawable inflateXmlDrawable(Resources res, XmlElement xml) {
        AttributeList attrs = res.obtainStyled(xml);
        switch (xml.name()){
            case "layer-list":{
                LayerListDrawable drawable = new LayerListDrawable();
                for (int i = 0; i < xml.childCount(); i++){
                    XmlElement child = xml.childAt(i);
                    Drawable childDrawable;
                    Rect padding = new Rect();
                    if (child.has("src")){
                        childDrawable = parseDrawable(res, child.attr("src"));
                    } else {
                        childDrawable = inflateXmlDrawable(res, child.childAt(0));
                    }
                    attrs = res.obtainStyled(child);
                    attrs.searchDimension(AttrList.padding, v -> {
                        int x = Math.round(v);
                        padding.set(x,x,x,x);
                    });
                    attrs.searchDimension("left", v -> padding.left = Math.round(v));
                    attrs.searchDimension("right", v -> padding.right = Math.round(v));
                    attrs.searchDimension("bottom", v -> padding.bottom = Math.round(v));
                    attrs.searchDimension("top", v -> padding.top = Math.round(v));
                    drawable.addLayer(childDrawable, padding);
                }
                return drawable;
            }
            case "shape":{
                ShapeDrawable drawable = new ShapeDrawable();;
                attrs.searchColor(AttrList.color, drawable::setColor);
                attrs.searchDimension(AttrList.stroke, drawable::setStrokeSize);
                String shapeType = xml.has("type") ? xml.attr("type") : "rect";

                switch (shapeType){
                    case "rounded": {
                        RoundedShape shape = new RoundedShape();
                        attrs.searchDimension(AttrList.radius, v -> shape.setRadius(Math.round(v)));
                        drawable.setShape(shape);
                    }break;
                    case "circle":
                    case "oval": {
                        drawable.setShape(new OvalShape());
                    }break;
                    default:
                        drawable.setShape(new RectShape());
                        break;
                }
                return drawable;
            }
            case "selector": {
                SelectorDrawable drawable = new SelectorDrawable();
                for (int i = 0; i < xml.childCount(); i++){
                    XmlElement item = xml.childAt(i);
                    HashMap<String, String> stateAttrs = new HashMap<>(item.attrs());
                    Drawable child;
                    if (item.has("src")) {
                        child = parseDrawable(res, item.attr("src"));
                    } else {
                        child = inflateXmlDrawable(res, item.childAt(0));
                    }

                    stateAttrs.remove("src");
                    StateList states = new StateList();
                    for (String key: stateAttrs.keySet()){
                        states.set(StateList.fromName(key), Boolean.parseBoolean(stateAttrs.get(key)));
                    }
                    drawable.add(child, states);
                }
                return drawable;
            }
            case "color":{
                ColorDrawable drawable = new ColorDrawable(0);
                attrs.searchColor(AttrList.color, drawable::setColor);
                return drawable;
            }
            default:
                throw new RuntimeException("Cannot support drawable type: "+xml.name());
        }
    }

    public static Drawable parseDrawable(Resources resources, String value) {
        if (value.startsWith("#")){
            return new ColorDrawable(Color.parseColor(value));
        } else if (value.startsWith("@")){
            value = value.substring(1);
            if (Resources.Manager.exists(value+".xml")){
                return inflateXmlDrawable(resources, resources.requestXml(value));
            } else {
                String[] supportedImages = new String[]{
                        "png", "jpg", "jpeg"
                };
                for (String format: supportedImages){
                    if (Resources.Manager.exists(value+"."+format)){
                        return new BitmapDrawable(BitmapFactory.openBitmap(Resources.Manager.openDocument(value+"."+format)));
                    }
                }
            }
        } else {
            Log.error(LOG_TAG,"Invalid background value: "+value);
        }
        return null;
    }
}