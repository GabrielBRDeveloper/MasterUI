package br.nullexcept.mux.test;

import br.nullexcept.mux.app.Activity;
import br.nullexcept.mux.app.Application;
import br.nullexcept.mux.utils.Log;
import br.nullexcept.mux.lang.xml.XmlElement;

public class Example extends Activity {
    public static void main(String[] args) {
        Application.initialize(Example::new);
    }

    private void printXml(String space, XmlElement element){
        System.out.println(space+"   ["+element.name()+"]");
        for (String key: element.attrNames()){
            System.out.println(space+" - "+key+" = "+element.attr(key));
        }
        if (element.childCount() > 0) {
            System.out.println(space + " - Children: ");
            for (int i = 0; i < element.childCount(); i++) {
                printXml(space + "   ", element.childAt(i));
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.log("OK", "Hello world");
        setContentView("main");
        setTitle("Example");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
