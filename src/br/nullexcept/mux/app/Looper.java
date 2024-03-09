package br.nullexcept.mux.app;

import java.util.HashMap;

public class Looper {
    private final HashMap<Long, Runnable> executions = new HashMap<>();
    private boolean stop = false;

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
                        System.err.println("ERROR ON EXECUTE: " + e);
                    }
                }
            }
        }
    }

    public void stop(){
        this.stop = true;
    }
}
