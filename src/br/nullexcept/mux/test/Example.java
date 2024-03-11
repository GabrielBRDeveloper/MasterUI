package br.nullexcept.mux.test;

import br.nullexcept.mux.app.Activity;
import br.nullexcept.mux.app.Application;
import br.nullexcept.mux.graphics.Color;
import br.nullexcept.mux.graphics.drawable.ColorDrawable;
import br.nullexcept.mux.widget.TextView;

public class Example extends Activity {
    public static void main(String[] args) {
        Application.initialize(new Example());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("On create");
        TextView view = new TextView(this);
        setTitle("Hello world!");
        view.setText("Hello view!");
        view.setTextSize(18.0f);
        view.setBackground(new ColorDrawable(Color.BLACK));
        setContentView(view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Application.stop();
    }
}
