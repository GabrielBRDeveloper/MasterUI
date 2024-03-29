package br.nullexcept.mux.app;

import br.nullexcept.mux.lang.Log;

import java.util.HashMap;

public class Looper {
    private static final String LOG_TAG = "Looper";

    private final HashMap<Long, Runnable> executions = new HashMap<>();
    static Looper mainLooper;
    private boolean stop = false;

    public static Looper getMainLooper() {
        return mainLooper;
    }

    public void initialize(){
        executions.clear();
        stop = false;
    }

    public void postDelayed(Runnable runnable, long msTime){
        executions.put(System.nanoTime()+(msTime*1000000L), runnable);
    }

    public void post(Runnable runnable){
        postDelayed(runnable, 1);
    }

    public void loop(){
        while (!stop) {
            long time = System.nanoTime();
            Long[] keys = executions.keySet().toArray(new Long[0]);
            for (long key : keys) {
                if (key <= time) {
                    Runnable runnable = executions.get(key);
                    executions.remove(key);
                    try {
                        runnable.run();
                    } catch (Throwable e) {
                        Log.error(LOG_TAG, "[ERROR ON LOOPER]");
                        Log.error(LOG_TAG, e);
                        stop = true;
                    }
                }
            }
        }
    }

    public void stop(){
        this.stop = true;
    }
}
