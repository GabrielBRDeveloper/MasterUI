package br.nullexcept.mux.test;

import br.nullexcept.mux.app.Activity;
import br.nullexcept.mux.app.Application;
import br.nullexcept.mux.app.Launch;
import br.nullexcept.mux.app.Project;
import br.nullexcept.mux.lang.Promise;
import br.nullexcept.mux.utils.Log;
import br.nullexcept.mux.view.View;

public class Example extends Activity {
    private static int layer = 0;
    public static void main(String[] args) {
        Application.initialize(Project.build(new Launch<>(Example.class), "br.nullexcept.test-master-ui"));
    }
    private View animate;
    int x = 0;
    int z = 955;

    @Override
    public void onCreate() {
        super.onCreate();

        if (layer == 0) {
            postDelayed(() -> {
            //     startActivity(new Launch<>(Example.class).addFlags(FLAG_ACTIVITY_NEW_WINDOW));
            }, 1);
        }
        layer++;
        setContentView("@ex-layout/main");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
