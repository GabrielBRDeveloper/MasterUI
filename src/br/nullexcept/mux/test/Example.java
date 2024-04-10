package br.nullexcept.mux.test;

import br.nullexcept.mux.app.Activity;
import br.nullexcept.mux.app.Application;
import br.nullexcept.mux.utils.Log;
import br.nullexcept.mux.lang.xml.XmlElement;

public class Example extends Activity {
    public static void main(String[] args) {
        Application.initialize(Example::new);
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
