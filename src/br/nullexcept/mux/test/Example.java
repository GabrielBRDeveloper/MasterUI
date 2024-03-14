package br.nullexcept.mux.test;

import br.nullexcept.mux.app.Activity;
import br.nullexcept.mux.app.Application;
import br.nullexcept.mux.app.Looper;
import br.nullexcept.mux.graphics.Bitmap;
import br.nullexcept.mux.graphics.BitmapFactory;
import br.nullexcept.mux.graphics.Color;
import br.nullexcept.mux.graphics.drawable.BitmapDrawable;
import br.nullexcept.mux.graphics.drawable.ColorDrawable;
import br.nullexcept.mux.hardware.CharEvent;
import br.nullexcept.mux.input.KeyEvent;
import br.nullexcept.mux.res.AssetsManager;
import br.nullexcept.mux.view.Gravity;
import br.nullexcept.mux.view.View;
import br.nullexcept.mux.view.ViewGroup;
import br.nullexcept.mux.widget.EditText;
import br.nullexcept.mux.widget.LinearLayout;
import br.nullexcept.mux.widget.TextView;

public class Example extends Activity {
    public static void main(String[] args) {
        Application.initialize(new Example());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Bitmap bitmap = BitmapFactory.openBitmap(AssetsManager.openDocument("panda.png"));
        LinearLayout layout = new LinearLayout(this);
        TextView view = new TextView(this);
        setTitle("Hello world!");
        view.setText("Label");

        LinearLayout leftBar = new LinearLayout(this);
        leftBar.setBackground(new ColorDrawable(0xFF404040));
        LinearLayout content = new LinearLayout(this);
        content.setBackground(new ColorDrawable(0xFF383838));

        leftBar.setLayoutParams(new ViewGroup.LayoutParams(256, ViewGroup.LayoutParams.MATCH_PARENT));
        content.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        view.setTextSize(40.0f);
        view.setGravity(Gravity.CENTER);
        view.setPadding(10,10,10,10);

        View imageView = new View(this);
        imageView.setBackground(new BitmapDrawable(bitmap));
        imageView.setLayoutParams(new ViewGroup.LayoutParams(256,140));
        imageView.setFocusable(true);
        imageView.setOnClickListener((vw)->{
            System.err.println("CLICKED ON PANDA");
        });

        EditText edit = new EditText(this);
        edit.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        edit.setPadding(20,20,20,20);
        edit.setBackground(new ColorDrawable(Color.BLACK));

        content.setPadding(20,20,20,20);

        content.addChild(view);
        content.addChild(imageView);
        content.addChild(edit);
        layout.setOrientation(LinearLayout.ORIENTATION_HORIZONTAL);
        layout.addChild(leftBar);
        layout.addChild(content);
        setContentView(layout);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Application.stop();
    }
}
