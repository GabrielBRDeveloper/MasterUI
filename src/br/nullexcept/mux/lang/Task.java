package br.nullexcept.mux.lang;

public class Task extends Thread {
    private boolean running = false;
    private final Runnable callback;

    public Task(Runnable run) {
        super();
        this.callback = run;
    }

    @Override
    public final void run() {
        super.run();
        running = true;
        callback.run();
        running = false;
    }

    public void start() {
        running = true;
        super.start();
    }

    public boolean isRunning() {
        return running;
    }

    public void waitFor() {
        if (!running) return;
        try {
            join();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
