package br.nullexcept.mux.res;

import br.nullexcept.mux.graphics.Drawable;
import br.nullexcept.mux.lang.xml.XmlElement;
import br.nullexcept.mux.view.AttrList;
import br.nullexcept.mux.view.menu.MenuGroup;
import br.nullexcept.mux.view.menu.MenuItem;

import java.util.Arrays;

public class MenuInflater {
    private final Resources res;
    MenuInflater(Resources res) {
        this.res = res;
    }

    public MenuItem inflate(String id) {
        if (!id.startsWith("@")) {
            id = "@menu/"+id;
        }
        XmlElement xml = res.requestXml(id.substring(1));
        return inflate(xml);
    }

    private MenuItem inflate(XmlElement xml) {
        AttributeList resolver = res.obtainStyled(xml);
        if ("group".equals(xml.name())) {
            MenuGroup group = new MenuGroup(resolver.getRawValue("id"));
            for (XmlElement child: xml.children()) {
                group.add(inflate(child));
            }
            return group;
        } else {
            String[] info = new String[2];
            Drawable[] icon = new Drawable[1];
            Arrays.fill(info, "");

            resolver.searchText(AttrList.title, (text) -> info[0] = String.valueOf(text));
            resolver.searchText(AttrList.summary, (text) -> info[1] = String.valueOf(text));
            resolver.searchDrawable(AttrList.icon, (ic) -> icon[0] = ic);

            return new MenuItem(resolver.getRawValue("id"),info[0],info[1], icon[0]);
        }
    }
}
