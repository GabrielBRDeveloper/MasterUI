package br.nullexcept.mux.app;

import br.nullexcept.mux.C;
import br.nullexcept.mux.core.texel.TexelAPI;
import br.nullexcept.mux.lang.Function;
import br.nullexcept.mux.lang.Valuable;
import br.nullexcept.mux.res.AssetsManager;
import br.nullexcept.mux.res.Resources;
import br.nullexcept.mux.view.Window;
import org.lwjgl.opengles.GLES;

import static org.lwjgl.glfw.GLFW.*;

public class Application {
    private static long lastGc = System.currentTimeMillis();

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
        loop.postDelayed(()->boot(creator.get()), 0);
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
        Looper.getMainLooper().post(Application::loop);
    }

    private static void boot(Activity activity) {
        Window window = TexelAPI.createWindow(activity);
        activity.mWindow = window;
        window.setVisible(true);
    }

    public static void stop(){
        Looper.getMainLooper().stop();
    }
}
