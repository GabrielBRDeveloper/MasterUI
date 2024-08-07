package br.nullexcept.mux.res;

import br.nullexcept.mux.graphics.*;
import br.nullexcept.mux.graphics.drawable.*;
import br.nullexcept.mux.graphics.shape.OvalShape;
import br.nullexcept.mux.graphics.shape.RectShape;
import br.nullexcept.mux.graphics.shape.RoundedShape;
import br.nullexcept.mux.lang.xml.XmlElement;
import br.nullexcept.mux.utils.Log;
import br.nullexcept.mux.view.AttrList;
import br.nullexcept.mux.view.Gravity;

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

                    Size size = new Size(-1,-1);
                    int[] gravity = new int[]{ Gravity.LEFT };

                    attrs.searchDimension(AttrList.padding, v -> padding.set(v.intValue(),v.intValue(),v.intValue(),v.intValue()));
                    attrs.searchDimension(AttrList.width, v -> size.width = v.intValue());
                    attrs.searchDimension(AttrList.height, v -> size.height = v.intValue());
                    attrs.searchRaw(AttrList.gravity, v -> gravity[0] = Gravity.parseGravity(v));

                    attrs.searchDimension("left", v -> padding.left = Math.round(v));
                    attrs.searchDimension("right", v -> padding.right = Math.round(v));
                    attrs.searchDimension("bottom", v -> padding.bottom = Math.round(v));
                    attrs.searchDimension("top", v -> padding.top = Math.round(v));

                    drawable.addLayer(childDrawable, padding, size, gravity[0]);
                }
                return drawable;
            }
            case "path": {
                int width = Integer.parseInt(attrs.getRawValue("width"));
                int height = Integer.parseInt(attrs.getRawValue("height"));

                Path path = parseVectorPath(attrs.getRawValue("src"), width,height);
                PathDrawable drawable = new PathDrawable(path, width,height);
                attrs.searchColor(AttrList.color, drawable::setColor);
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
                        Rect rect = new Rect();
                        attrs.searchDimension(AttrList.radius, v -> rect.set(v.intValue(),v.intValue(),v.intValue(),v.intValue()));
                        attrs.searchDimension("leftTop", v -> rect.left = v.intValue());
                        attrs.searchDimension("rightTop", v -> rect.right = v.intValue());
                        attrs.searchDimension("leftBottom", v -> rect.top = v.intValue());
                        attrs.searchDimension("rightBottom", v -> rect.bottom = v.intValue());
                        shape.setRadius(rect);
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
            case "material-icon": {
                MaterialIconDrawable drawable  = new MaterialIconDrawable();
                attrs.searchRaw("name", drawable::setIcon);
                attrs.searchColor("color", drawable::setColor);

                return drawable;
            }
            case "nine-path": {
                NinePathDrawable drawable = new NinePathDrawable();
                drawable.setBitmap(openBitmap(attrs.getRawValue("src").substring(1)));
                drawable.setBorder(Integer.parseInt(attrs.getRawValue("border")));

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

    private static Path parseVectorPath(String src, int w, int h) {
        /*String code =
                "<svg xmlns=\"http://www.w3.org/2000/svg\" " +
                        "width=\""+w+"\" " +
                        "height=\""+h+"\" " +
                        "viewBox=\"0 0 "+w+" "+h+"\">" +
                        "<path d=\""+src+"\"/>" +
                "</svg>";

        NSVGImage img = NanoSVG.nsvgParse(code, "px", 24);
        NSVGShape shape = img.shapes();
        NSVGPath _path = shape.paths();
        ArrayList<NSVGPath> paths = new ArrayList<>();
        while (_path != null) {
            paths.add(_path);
            try {
                _path = _path.next();
            } catch (Exception e) {
                break;
            }
        }


        Path p = new Path();

        ArrayList<float[]> points = new ArrayList<>();
        for (NSVGPath path : paths) {
            FloatBuffer buffer = path.pts();
            float bx = buffer.get(0);
            float by = buffer.get(1);

            for (int x = 0; x < path.npts() - 1; x += 3) {
                int i = x * 2;
                points.add(new float[]{
                        buffer.get(i + 2), buffer.get(i + 3),
                        buffer.get(i + 4), buffer.get(i + 5),
                        buffer.get(i + 6), buffer.get(i + 7)
                });
            }

            p.add(bx, by, path.closed() == 1, points.toArray(new float[0][0]));
            points.clear();
        }
        return p;*/
        return null;
    }

    private static Bitmap openBitmap(String path) {
        String[] supportedImages = new String[]{
                "png", "jpg", "jpeg"
        };
        for (String format: supportedImages){
            if (Resources.Manager.exists(path+"."+format)){
                return BitmapFactory.openBitmap(Resources.Manager.openDocument(path+"."+format));
            }
        }
        return null;
    }

    public static Drawable parseDrawable(Resources resources, String value) {
        value = value.trim();
        if (value.startsWith("#")){
            return new ColorDrawable(Color.parseColor(value));
        } else {
            if (value.startsWith("@")) {
                value = value.substring(1);
            }
            if (Resources.Manager.exists(value+".xml")){
                return inflateXmlDrawable(resources, resources.requestXml(value));
            } else {
                Bitmap res = openBitmap(value);
                if (res != null) {
                    return new BitmapDrawable(res);
                }
            }
        }
        Log.error(LOG_TAG,"Invalid drawable source value: "+value);
        return null;
    }

    public static CharSequence parseText(Resources resources, String id) {
        if (id.startsWith("@string/")) {
            return resources.getString(id.substring("@string/".length()));
        }
        return id;
    }

    public static ColorStateList parseColorState(Resources resources, String value) {
        if (value.startsWith("#")) {
            return new ColorStateList(parseColor(value));
        } else if (value.startsWith("@color/")) {
            XmlElement xml = resources.requestXml(value.substring(1));
            ColorStateList color = new ColorStateList(xml.has("default") ? parseColor(xml.attr("default")) : Color.TRANSPARENT);
            for (int i = 0; i < xml.childCount(); i++) {
                XmlElement child = xml.childAt(i);
                HashMap<String, String> attrs = new HashMap<>(child.attrs());
                StateList states = new StateList();
                for (String key: attrs.keySet()){
                    states.set(StateList.fromName(key), Boolean.parseBoolean(attrs.get(key)));
                }
                color.add(parseColor(child.value()),states);
            }
        }
        Log.error(LOG_TAG, "Invalid color value: "+value);
        return new ColorStateList(Color.RED);
    }
}