package br.nullexcept.mux.res;

import br.nullexcept.mux.graphics.BitmapFactory;
import br.nullexcept.mux.graphics.Color;
import br.nullexcept.mux.graphics.Drawable;
import br.nullexcept.mux.graphics.drawable.BitmapDrawable;
import br.nullexcept.mux.graphics.drawable.ColorDrawable;
import br.nullexcept.mux.graphics.drawable.LayerListDrawable;
import br.nullexcept.mux.graphics.drawable.ShapeDrawable;
import br.nullexcept.mux.graphics.shape.OvalShape;
import br.nullexcept.mux.graphics.shape.RectShape;
import br.nullexcept.mux.graphics.shape.RoundedShape;
import br.nullexcept.mux.graphics.shape.Shape;
import br.nullexcept.mux.lang.Log;
import br.nullexcept.mux.lang.xml.XmlElement;
import br.nullexcept.mux.view.AttrList;
import org.w3c.dom.Attr;

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
                    if (child.has("src")){
                        drawable.addLayer(parseDrawable(res, child.attr("src")));
                    } else {
                        drawable.addLayer(inflateXmlDrawable(res, child.childAt(0)));
                    }
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
            if (AssetsManager.exists(value+".xml")){
                return inflateXmlDrawable(resources, resources.requestXml(value));
            } else {
                String[] supportedImages = new String[]{
                        "png", "jpg", "jpeg"
                };
                for (String format: supportedImages){
                    if (AssetsManager.exists(value+"."+format)){
                        return new BitmapDrawable(BitmapFactory.openBitmap(AssetsManager.openDocument(value+"."+format)));
                    }
                }
            }
        } else {
            Log.error(LOG_TAG,"Invalid background value: "+value);
        }
        return null;
    }
}