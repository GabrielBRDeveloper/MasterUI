package br.nullexcept.mux.test;

import br.nullexcept.mux.graphics.Color;
import br.nullexcept.mux.graphics.drawable.ColorDrawable;
import br.nullexcept.mux.renderer.program.GLShaderList;
import br.nullexcept.mux.renderer.texel.GLTexel;
import br.nullexcept.mux.widget.AbsoluteLayout;
import br.nullexcept.mux.view.RootViewGroup;
import br.nullexcept.mux.view.View;
import br.nullexcept.mux.view.ViewGroup;
import br.nullexcept.mux.widget.TextView;

import static br.nullexcept.mux.hardware.GLES.*;

public class TestGFW extends GlesWindow {

    private double[] rgb = new double[3];
    private int i = 0;
    private int v = 0;
    private int frames = 0;
    private long lastTime = 0;
    private int[][] sizes = new int[2][1];
    private RootViewGroup root;
    private View external;
    private TextView text;

    public static void main(String[] args) {
        new TestGFW().init();
    }

    @Override
    protected void create() {
        GLShaderList.build();
        root = new RootViewGroup(null);
        root.setBackground(new ColorDrawable(Color.RED));

        AbsoluteLayout view = new AbsoluteLayout(null);
        view.setLayoutParams(new ViewGroup.LayoutParams(512,512));
        view.setBackground(new ColorDrawable(Color.GREEN));
        root.addChild(view);

        external = view;
        for (int i = 0; i < 32; i++){
            for (int x = 0; x < 32; x++) {
                View view2 = new View(null);
                view2.setLayoutParams(new AbsoluteLayout.LayoutParams(i * 32, x * 32, 20, 20));
                view2.setBackground(new ColorDrawable(Color.BLUE));
                //view.addChild(view2);
            }
        }

        text = new TextView(null);
        text.setAlpha(1f);
        text.setTextSize(32.0f);
        text.setText("Hello world!!!\nHello my friends");
        //text.setLayoutParams(new AbsoluteLayout.LayoutParams(300,300));
        view.addChild(text);
    }

    float[] bars = new float[100];
    float[] mul = new float[100];
    private int fps;

    @Override
    protected void draw() {
        glViewport(0,0,getWidth(),getHeight());
        rgb[v] += 0.04;
        i++;
        if (i % 25 == 0){
            v++;
            if (v >= 3){
                v = 0;
            }
        }

        frames++;
        if (System.currentTimeMillis() - lastTime >= 1000){
            fps = frames;
            frames = 0;
            lastTime = System.currentTimeMillis();
            System.out.println("fps: "+fps);
        }

        root.draw();
        glClearColor(.0f,0f,0f,1f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        GLTexel.drawTexture(0,0,512,512, root.getCanvas().getFramebuffer().getTexture());
    }

    @Override
    protected void dispose() {

    }
}