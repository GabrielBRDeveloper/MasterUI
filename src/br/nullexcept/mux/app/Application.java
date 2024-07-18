package br.nullexcept.mux.app;

import br.nullexcept.mux.core.texel.TexelAPI;
import br.nullexcept.mux.view.Window;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Application {
    private static long lastGc = System.currentTimeMillis();
    private static final HashMap<String, Service> services = new HashMap<>();
    private static final ArrayList<ActivityStack> activities = new ArrayList<>();
    private static Project current;

    public static void initialize(Project project){
        initialize(project, new TexelAPI());
    }
    public static void initialize(Project project, CoreBoostrap boostrap){
        Application.current = project;
        Looper loop = new Looper();
        Looper.mainLooper = loop;
        Files.build();
        loop.initialize();
        boostrap.boot();

        Activity nw = project.getLaunch().make();
        nw.stack = new ActivityStack(nw);
        loop.postDelayed(()->boot(boostrap.makeWindow(), nw), 0);
        loop.post(Application::loop);
        loop.loop();
        boostrap.finish();
        System.gc();
        Looper.sleep(2000); // Wait for all services stop
        System.exit(0);
    }

    private static void loop(){
        if (System.currentTimeMillis() - lastGc > 5000){
            System.gc();
            lastGc = System.currentTimeMillis();
        }
        if (activities.size() == 0) { // Stop if contains 0 activity
            stop();
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

    static synchronized  <T extends Service> T beginService(Launch<T> launch) {
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


    protected static class Files {
        public static File DEVICE_DATA;
        public static File APP_DIR;

        private static void build() {
            String appId = "";
            {
                final String allowed = " ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789._-";
                String pack = current.getPackage() + "";
                for (int i = 0; i < pack.length(); i++) {
                    if (allowed.indexOf(pack.charAt(i)) >= 0) {
                        appId += pack.charAt(i);
                    } else {
                        appId += '.';
                    }
                }
            }
            Map<String, String> env = System.getenv();
            { // Obtain app data dir
                if (env.containsKey("APPDATA")) {
                    DEVICE_DATA = new File(env.get("APPDATA")); // WINDOWS DEVICE
                } else if (env.containsKey("HOME")) { // UNIX DEVICES (MAC-OS, LINUX, UNIX)
                    DEVICE_DATA = new File(env.get("HOME"), ".local/share");
                } else {
                    DEVICE_DATA = new File(System.getProperty("user.home"), ".var");
                }
                DEVICE_DATA.mkdirs();
                APP_DIR = new File(DEVICE_DATA, "MasterApps/" + appId).getAbsoluteFile();
                APP_DIR.mkdirs();
            }
        }
    }
}
