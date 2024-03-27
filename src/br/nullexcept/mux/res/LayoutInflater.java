package br.nullexcept.mux.res;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.lang.Function;
import br.nullexcept.mux.lang.ValuedFunction;
import br.nullexcept.mux.lang.xml.XmlElement;
import br.nullexcept.mux.view.View;
import br.nullexcept.mux.view.ViewGroup;
import br.nullexcept.mux.widget.*;

import java.util.HashMap;

import static br.nullexcept.mux.view.AttrList.*;

public class LayoutInflater {
    private static final HashMap<String, ViewRegister> registers = new HashMap<>();
    private final Context context;

    LayoutInflater(Context context){
        this.context = context;
    }

    public <T extends View> T inflate(String resource){
        Resources res = context.getResources();
        XmlElement element = res.requestXml("layout/" +resource);
        return inflate(element);
    }


    private  <T extends View> T inflate(XmlElement xml){
        if ("include".equals(xml.name())){
            return inflate(xml.attr("layout"));
        }
        Resources res = context.getResources();
        FallbackAttributes agent = new FallbackAttributes(xml, (FallbackAttributes) res.obtainStyled("Widget."+xml.name()), res);
        if (!registers.containsKey(xml.name())){
            throw new RuntimeException("Invalid view class "+xml.name()+" you need register class before use.");
        }
        ViewRegister register = registers.get(xml.name());
        View view = register.create(context,agent);
        view.setLayoutParams(parseLayoutParams(agent));
        if (view instanceof ViewGroup){
            for (int i = 0; i < xml.childCount(); i++){
                ((ViewGroup) view).addChild(inflate(xml.childAt(i)));
            }
        } else if (xml.childCount() > 0){
            throw new IllegalArgumentException("Child "+xml.name()+" is not view group and contains children");
        }
        return (T) view;
    }

    private ViewGroup.LayoutParams parseLayoutParams(AttributeList attr){
        ViewGroup.LayoutParams params;
        if (attr.contains(x)){
            AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(0,0);
            attr.searchDimension(x, v -> lp.x = v.intValue());
            attr.searchDimension(y, v -> lp.y = v.intValue());
            params = lp;
        } else {
            params = new ViewGroup.LayoutParams(0,0);
        }

        ValuedFunction<String, Integer> parseParams = (key)->{
            int[] result = new int[]{Integer.MIN_VALUE};
            attr.searchRaw(key, (value)->{
                switch (value){
                    case "match_parent":
                        result[0] = ViewGroup.LayoutParams.MATCH_PARENT;
                        break;
                    case "wrap_content":
                        result[0] = ViewGroup.LayoutParams.WRAP_CONTENT;
                        break;
                    default:
                        attr.searchDimension(key, x -> {
                            result[0] = Math.round(x);
                        });
                }
            });
            if (result[0] == Integer.MIN_VALUE) {
                throw new IllegalArgumentException("Not found "+key+" params");
            }
            return result[0];
        };

        params.width = parseParams.run(width);
        params.height = parseParams.run(height);

        return params;
    }

    static {
        registerView("AbsoluteLayout", AbsoluteLayout::new);
        registerView("LinearLayout", LinearLayout::new);

        registerView("ImageView", ImageView::new);
        registerView("EditText", EditText::new);
        registerView("Button", Button::new);
        registerView("TextView", TextView::new);
        registerView("Switch", Switch::new);
        registerView("View", View::new);
    }

    public static void registerView(String name, ViewRegister register){
        registers.put(name, register);
    }

    public interface ViewRegister {
        View create(Context ctx, AttributeList attr);
    }
}
