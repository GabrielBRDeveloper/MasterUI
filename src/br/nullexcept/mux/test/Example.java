package br.nullexcept.mux.test;

import br.nullexcept.mux.app.Activity;
import br.nullexcept.mux.app.Application;
import br.nullexcept.mux.app.Launch;
import br.nullexcept.mux.app.Project;
import br.nullexcept.mux.lang.Promise;
import br.nullexcept.mux.utils.Log;
import br.nullexcept.mux.view.View;

public class Example extends Activity {
    public static void main(String[] args) {
        Application.initialize(Project.build(new Launch<>(Example.class), "br.nullexcept.test-master-ui"));
    }
    private View animate;
    int x = 0;
    int z = 955;

    @Override
    public void onCreate() {
        super.onCreate();
        setContentView("@ex-layout/main");
        animate = findViewById("animate");
        update();
    }

    void update() {
        animate.getTransition().getTranslate().set((clamp(x, 100))-50,0);
        animate.getTransition().getScale().set(clamp(z, 200)/100.0f, 1.0f);
        animate.getTransition().setRotation(clamp(z+126, 360));
        x++;
        z++;
        animate.invalidate();
        postDelayed(this::update, 15);
    }

    public int clamp(int value, int max) {
        int rest = value % max;
        int div = value / max;

        return div % 2 == 0 ? rest : (max - rest);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
