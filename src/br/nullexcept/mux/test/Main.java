package br.nullexcept.mux.test;

import br.nullexcept.mux.app.Activity;
import br.nullexcept.mux.app.Application;
import br.nullexcept.mux.graphics.Color;
import br.nullexcept.mux.graphics.drawable.ColorDrawable;
import br.nullexcept.mux.view.View;
import br.nullexcept.mux.view.ViewGroup;

public class Main extends Activity {
    public static void main(String[] args) {
        Application.initialize(new Main());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("On create");
        View view = new View(this);
        setTitle("Hello world!");
        view.setBackground(new ColorDrawable(Color.RED));
        view.setLayoutParams(new ViewGroup.LayoutParams(400, 400));
        setContentView(view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Application.stop();
    }
}
