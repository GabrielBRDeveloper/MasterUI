package br.nullexcept.mux.app;

import br.nullexcept.mux.C;
import br.nullexcept.mux.core.texel.TexelAPI;
import br.nullexcept.mux.lang.Valuable;
import br.nullexcept.mux.view.Window;
import org.lwjgl.opengles.GLES;

import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.*;

public class Application {
    private static long lastGc = System.currentTimeMillis();
    private static final HashMap<String, Service> services = new HashMap<>();
    private static final ArrayList<ActivityStack> activities = new ArrayList<>();

    public static void initialize(Valuable<Activity> creator){
        glfwInit();
        glfwDefaultWindowHints();
        if (C.Config.SET_WINDOW_GL_HINT) {
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, C.Config.WINDOW_GL_VERSION[0]);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, C.Config.WINDOW_GL_VERSION[1]);
            glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_ES_API);
        }
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
        Activity nw = creator.get();
        nw.stack = new ActivityStack(nw);
        loop.postDelayed(()->boot(TexelAPI.createWindow(), nw), 0);
        loop.post(Application::loop);
        loop.loop();
        TexelAPI.destroy();
        glfwTerminate();
        System.gc();
        Looper.sleep(2000,0); // Wait for all services stop
        System.exit(0);
    }

    private static void loop(){
        glfwPollEvents();
        if (System.currentTimeMillis() - lastGc > 5000){
            System.gc();
            lastGc = System.currentTimeMillis();
        }
        if (activities.size() == 0) { // Stop if contains 0 activity
            Looper.getMainLooper().stop();
            return;
        }
        synchronized (activities) {
            ArrayList<ActivityStack> stack2 = new ArrayList<>(activities);
            for (ActivityStack stack: stack2) {
                if (stack == null || !stack.isValid()) {
                    activities.remove(stack);
                }
            }
        }
        Looper.getMainLooper().post(Application::loop);
    }

    static Window.WindowObserver buildObserver(Activity activity) {
        synchronized (activities) {
            activities.add(activity.stack);
        }
        return new Window.WindowObserver() {
            @Override
            public void onCreated() {
                activity.onCreate();
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
                ArrayList<ActivityStack> stacks = new ArrayList<>();
                ActivityStack stack = activity.stack;
                while (stack != null) {
                    stacks.add(0, stack);
                    stack = stack.getBackItem();
                }

                // Fire all stack list
                stacks.forEach((item)-> {
                    if (item.isValid()) {
                        item.getActivity().finish();
                    }
                });
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
        for (Service service: services.values())
            service.myLooper.stop();
        Looper.getMainLooper().stop();
    }

    static <T extends Service> T beginService(Launch<T> launch) {
        String name = launch.getLaunchClass().getName();

        if (services.containsKey(name)) {
            Service service = services.get(name);
            service._args = launch;
            service.onParcelChanged(launch);
            return (T) service;
        }

        Service service = launch.make();

        Looper looper = new Looper();
        service.myLooper = looper;
        service._args = launch;

        services.put(name, service);
        new Thread(()->{
            looper.initialize();
            looper.post(service::onCreate);
            looper.loop();
            services.remove(name);
            service.onDestroy();
        }).start();
        return (T) service;
    }
}
