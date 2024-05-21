package br.nullexcept.mux.test;

import br.nullexcept.mux.app.Activity;
import br.nullexcept.mux.app.Application;
import br.nullexcept.mux.lang.Promise;
import br.nullexcept.mux.utils.Log;

public class Example extends Activity {
    public static void main(String[] args) {
        Application.initialize(Example::new);
    }

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
