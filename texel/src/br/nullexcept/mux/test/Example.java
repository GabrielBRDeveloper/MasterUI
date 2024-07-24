package br.nullexcept.mux.test;

import br.nullexcept.mux.app.Activity;
import br.nullexcept.mux.app.Application;
import br.nullexcept.mux.app.Launch;
import br.nullexcept.mux.core.texel.TexelProject;
import br.nullexcept.mux.view.View;

public class Example extends Activity {
    public static void main(String[] args) {
        Application.initialize(new TexelProject("br.nullexcept.test-master-ui",new Launch<>(Example.class)));
    }
    private View animate;
    int x = 0;
    int z = 955;

    @Override
    public void onCreate() {
        super.onCreate();
        setContentView("@ex-layout/main");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
