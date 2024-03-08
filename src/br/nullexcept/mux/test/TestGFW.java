package br.nullexcept.mux.test;

import br.nullexcept.mux.graphics.Canvas;
import br.nullexcept.mux.graphics.Color;
import br.nullexcept.mux.graphics.Paint;
import br.nullexcept.mux.graphics.Rect;
import br.nullexcept.mux.renderer.texel.CanvasTexel;
import br.nullexcept.mux.renderer.program.GLShaderList;
import br.nullexcept.mux.renderer.texel.GLTexel;

import static br.nullexcept.mux.hardware.GLES.*;

public class TestGFW extends GlesWindow {

    private double[] rgb = new double[3];
    private int i = 0;
    private int v = 0;
    private int frames = 0;
    private long lastTime = 0;
    private int[][] sizes = new int[2][1];
    private Canvas canvas;
    private final Paint paint = new Paint();
    private final Rect rect = new Rect();

    public static void main(String[] args) {
        new TestGFW().init();
    }

    @Override
    protected void create() {
        GLShaderList.build();
        canvas = new CanvasTexel(512,512);
        paint.setTextSize(30);
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

        canvas.begin();
        canvas.drawColor(Color.BLACK);

        paint.setColor(Color.WHITE);
        //drawBars();
        for (int i = 0; i < 40; i++) {
            canvas.drawText("Hello world", 0, (i * 70) + 120, paint);
        }

        canvas.drawText("FPS: "+fps, 10, canvas.getHeight()-32,paint);
        canvas.end();
        glClearColor(.0f,0f,0f,1f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        GLTexel.drawTexture(0,512,512,-512, ((CanvasTexel)canvas).getFramebuffer().getTexture());
    }

    public void drawBars(){
        for (int i = 0; i < bars.length; i++){
            if (bars[i] >= 500){
                mul[i] = -1.0f;
            } else if (bars[i] <= 5){
                mul[i] = 1.0f;
            }
            bars[i] += mul[i];
        }

        int i = 0;
        for (float x : bars) {
            rect.setPosition((i*5)+5, (int) x);
            rect.setSize(2, (int) (512-x));
            canvas.drawRect(rect, paint);
            i++;
        }
    }

    @Override
    protected void dispose() {

    }
}