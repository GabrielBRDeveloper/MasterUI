package br.nullexcept.mux.test;

import br.nullexcept.mux.app.Activity;
import br.nullexcept.mux.app.Application;
import br.nullexcept.mux.graphics.Bitmap;
import br.nullexcept.mux.graphics.BitmapFactory;
import br.nullexcept.mux.graphics.Drawable;
import br.nullexcept.mux.graphics.drawable.BitmapDrawable;
import br.nullexcept.mux.utils.Log;
import br.nullexcept.mux.view.View;
import br.nullexcept.mux.view.anim.AlphaAnimation;
import br.nullexcept.mux.view.anim.RotationAnimation;

public class Example extends Activity {
    public static void main(String[] args) {
        Application.initialize(Example::new);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.log("OK", "Hello world");
        setContentView("main");
        if (true)return;
        View panda = findViewById("panda");
        {
            RotationAnimation rot = new RotationAnimation(panda, 5000);
            rot.setLoop(true);
            rot.play();
        }
        {
            AlphaAnimation alph = new AlphaAnimation(panda, 2000);
            alph.setAlpha(0,1);
            alph.setLoop(true);
            alph.play();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
