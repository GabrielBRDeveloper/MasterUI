package br.nullexcept.mux.app;

import br.nullexcept.mux.C;
import br.nullexcept.mux.core.texel.TexelAPI;
import br.nullexcept.mux.lang.Valuable;
import br.nullexcept.mux.view.Window;
import org.lwjgl.opengles.GLES;

import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.*;

public class Application {
    private static long lastGc = System.currentTimeMillis();
    private static HashMap<String, Service> services = new HashMap<>();
    private static int RUNNING_ACTIVITIES = 0;

    public static void initialize(Valuable<Activity> creator){
        glfwInit();
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
        glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_ES_API);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

        long window = glfwCreateWindow(1,1,"[MasterUI - Core]",0, 0);
        glfwMakeContextCurrent(window);
        glfwSwapInterval(0);
        C.GLFW_CONTEXT = window;
        GLES.createCapabilities();
        TexelAPI.initialize();
        Looper loop = new Looper();
        Looper.mainLooper = loop;
        loop.initialize();
        loop.postDelayed(()->boot(TexelAPI.createWindow(), creator.get()), 0);
        loop.post(Application::loop);
        loop.loop();
        TexelAPI.destroy();
        glfwTerminate();
        System.gc();
        System.exit(0);
    }

    private static final void loop(){
        glfwPollEvents();
        if (System.currentTimeMillis() - lastGc > 5000){
            System.gc();
            lastGc = System.currentTimeMillis();
        }
        if (RUNNING_ACTIVITIES == 0) {
            stop();
        }
        Looper.getMainLooper().post(Application::loop);
    }

    static Window.WindowObserver buildObserver(Activity activity) {
        return new Window.WindowObserver() {
            @Override
            public void onCreated() {
                activity.onCreate();
                RUNNING_ACTIVITIES++;
            }

            @Override
            public void onVisibilityChanged(boolean visible) {
                if (visible) {
                    activity.onResume();
                } else {
                    activity.onPause();
                }
            }

            @Override
            public void onResize(int width, int height) {
            }

            @Override
            public void onDestroy() {
                RUNNING_ACTIVITIES--;
                activity.onDestroy();
            }
        };
    }

    static void boot(Window window, Activity activity) {
        window.destroy();
        window.setWindowObserver(buildObserver(activity));
        activity.mWindow = window;
        window.create();
        window.setVisible(true);
    }

    public static void stop(){
        Looper.getMainLooper().stop();
    }

    public static <T extends Service> T beginService(Class<T> clazz, Valuable<T> service) {
        String name = clazz.getName();
        if (services.containsKey(name)) {
            return (T) services.get(name);
        }
        Looper looper = new Looper();
        T sv = service.get();
        sv.myLooper = looper;

        services.put(name, sv);
        new Thread(()->{
            looper.post(sv::onCreate);
            looper.initialize();
            looper.loop();
            services.remove(name);
            sv.onDestroy();
        }).start();
        return sv;
    }
}
