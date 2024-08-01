package br.nullexcept.mux.app;

import br.nullexcept.mux.C;
import br.nullexcept.mux.app.base.CoreBoostrap;
import br.nullexcept.mux.app.base.Project;
import br.nullexcept.mux.res.Resources;
import br.nullexcept.mux.view.Window;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ApplicationRuntime extends Context {
    private final Looper looper;
    private final Project project;
    private final CoreBoostrap bootstrap;
    private long lastGc;
    private final HashMap<String, Service> services = new HashMap<>();
    private final ArrayList<ActivityStack> activities = new ArrayList<>();
    private Resources resources;
    private final File appDir;

    ApplicationRuntime(Project project) {
        this.bootstrap = project.getCoreBootstrap();
        this.looper = new Looper();
        this.project = project;
        this.appDir = findAppDir();
    }

    private File findAppDir() {
        {
            File DEVICE_DATA;
            String appId = "";
            {
                final String allowed = " ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789._-";
                String pack = project.getPackage() + "";
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
                File app = new File(DEVICE_DATA, "MasterApps/" + appId).getAbsoluteFile();
                app.mkdirs();
                return app;
            }
        }
    }

    CoreBoostrap getBootstrap() {
        return bootstrap;
    }

    void start() {
        looper.initialize();
        if (Looper.getMainLooper() == null) {
            Looper.mainLooper = looper;
        }
        bootstrap.boot();
        this.resources = new Resources(this);

        Activity nw = project.getLaunch().make();
        nw.setMainLooper(getMainLooper());
        nw.setAppRuntime(this);
        nw.stack = new ActivityStack(nw);
        looper.postDelayed(() -> boot(bootstrap.makeWindow(), nw), 0);
        looper.post(this::loop);
        looper.loop();
        bootstrap.finish();
        System.gc();
        Looper.sleep(2000); // Wait for all services stop
    }

    @Override
    public Looper getMainLooper() {
        return looper;
    }

    void boot(Window window, Activity activity) {
        window.reset();
        activity.setAppRuntime(this);
        activity.setMainLooper(getMainLooper());
        activity.mWindow = window;
        window.setWindowObserver(buildObserver(activity));
        window.create();
        window.setVisible(true);
    }

    synchronized <T extends Service> T beginService(Launch<T> launch) {
        synchronized (services) {
            String name = launch.getLaunchClass().getName();
            if (services.containsKey(name)) {
                Service service = services.get(name);
                service._args = launch;
                service.onParcelChanged(launch);
                return (T) service;
            }

            Service service = launch.make();

            Looper serviceLooper = new Looper();
            service.myLooper = serviceLooper;
            service.setAppRuntime(this);
            service.setMainLooper(getMainLooper());
            service._args = launch;

            services.put(name, service);
            new Thread(() -> {
                serviceLooper.initialize();
                serviceLooper.post(service::onCreate);
                serviceLooper.loop();
                services.remove(name);
                service.onDestroy();
            }).start();
            return (T) service;
        }
    }

    private void stop() {
        synchronized (services) {
            for (Service service : services.values()) {
                service.getLooper().stop();
            }
            services.clear();
            if (Looper.mainLooper == looper) {
                Looper.mainLooper = null;
            }
            looper.stop();
        }
    }

    @Override
    public <T extends Service> T startService(Launch<T> service) {
        return beginService(service);
    }

    private void loop() {
        if (System.currentTimeMillis() - lastGc > 5000 && C.Flags.AUTO_GC) {
            System.gc();
            lastGc = System.currentTimeMillis();
        }
        if (activities.size() == 0) { // Stop if contains 0 activity
            stop();
            return;
        }
        synchronized (activities) {
            ArrayList<ActivityStack> stack2 = new ArrayList<>(activities);
            for (ActivityStack stack : stack2) {
                if (stack == null || !stack.isValid()) {
                    activities.remove(stack);
                }
            }
        }
        looper.postDelayed(this::loop, 0);
    }

    public void destroy() {
        looper.post(this::stop);
    }

    public boolean isRunning() {
        return looper.isRunning();
    }

    @Override
    public Resources getResources() {
        return resources;
    }

    @Override
    public File getFilesDir() {
        return appDir;
    }

    @Override
    public <T extends Applet> T getApplet(String name) {
        return (T) bootstrap.getSystemApplets().get(name);
    }

    public Looper getLooper() {
        return looper;
    }


    Window.WindowObserver buildObserver(Activity activity) {
        synchronized (activities) {
            activities.add(activity.stack);
        }
        activity.setMainLooper(getMainLooper());
        activity.setAppRuntime(this);
        return new WindowObserver(activity);
    }

    private static class WindowObserver implements Window.WindowObserver {
        private final Activity activity;

        private WindowObserver(Activity activity) {
            this.activity = activity;
        }

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
            stacks.forEach((item) -> {
                if (item.isValid()) {
                    item.getActivity().finish();
                }
            });
        }
    }
}