package br.nullexcept.mux.app;

import java.util.ArrayList;
import java.util.HashMap;

public class Looper {
    private static final HashMap<Long, Looper> activeLoops = new HashMap<>();
    private final ArrayList<Callback> executions = new ArrayList<>();
    static Looper mainLooper;
    private boolean stop = false;


    public static Looper getCurrentLooper() {
        Thread current = Thread.currentThread();
        synchronized (activeLoops) {
            if (activeLoops.containsKey(current.getId())) {
                return activeLoops.get(current.getId());
            }
        }
        return getMainLooper();
    }

    public static Looper getMainLooper() {
        return mainLooper;
    }

    public void initialize(){
        executions.clear();
        stop = false;
    }

    public void postDelayed(Runnable runnable, long msTime){
        msTime = Math.max(0, msTime);
        synchronized (executions) {
            executions.add(new Callback(runnable,System.currentTimeMillis()+msTime));
        }
    }

    public void post(Runnable runnable){
        postDelayed(runnable, 1);
    }

    public void loop(){
        Long threadID = Thread.currentThread().getId();
        synchronized (activeLoops) {
            activeLoops.put(threadID, this);
        }
        while (!stop) {
            Callback[] list;
            int i = 0;
            synchronized (executions) {
                list = executions.toArray(new Callback[0]);
                for (Callback call: list) {
                    if (call.time <= System.currentTimeMillis()) {
                        executions.remove(call);
                    } else {
                        list[i] = null;
                    }
                    i++;
                }
            }
            for (Callback call: list) {
                if (call == null) continue;
                call.handle.run();
            }
            sleep(0, (int) (Math.random()*100));
        }
        synchronized (activeLoops) {
            activeLoops.remove(threadID);
        }
    }

    public static void sleep(int ms) {
        sleep(ms,0);
    }

    public static void sleep(int ms, int nano) {
        try {
            Thread.sleep(ms, nano);
        } catch (Exception e){}
    }

    public void stop(){
        this.stop = true;
    }

    private static class Callback {
        private static long current;
        private final Runnable handle;
        private final long time;
        private final long id = hash();

        private Callback(Runnable handle, long time) {
            this.handle = handle;
            this.time = time;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Callback) {
                return ((Callback) obj).id == id;
            }
            return false;
        }

        public static synchronized long hash() {
            current++;
            return current;
        }
    }
}
