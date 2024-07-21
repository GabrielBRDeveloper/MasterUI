package br.nullexcept.mux.lang;

import java.util.ArrayList;

public class Promise<T> {
    public static final int STATE_RUNNING = 0;
    public static final int STATE_ERROR = 1;
    public static final int STATE_DONE = 2;

    private T result = null;
    private Throwable err;
    private int state = STATE_RUNNING;
    private final ArrayList<Function<T>> thenList = new ArrayList<>();
    private final ArrayList<Function<Throwable>> catchList = new ArrayList<>();
    private final Task thread;

    public Promise(PromiseRunnable<T> run) {
        thread = new Task(()->{
            try {
                result = run.execute();
                synchronized (thenList) {
                    state = STATE_DONE;
                }
                thenList.forEach((then)->then.call(result));
                thenList.clear();
            } catch (Throwable throwable) {
                synchronized (catchList) {
                    err = throwable;
                    state = STATE_ERROR;
                }
                catchList.forEach((cat)->cat.call(throwable));
                catchList.clear();
            }
        });
        thread.start();
    }

    public boolean isRunning() {
        return state == STATE_RUNNING;
    }

    public boolean isSuccess() {
        return state == STATE_DONE;
    }

    public Promise<T> fail(Function<Throwable> error) {
        synchronized (catchList) {
            if (state == STATE_RUNNING) {
                catchList.add(error);
            } else if (state == STATE_ERROR) {
                new Task(()-> error.call(err)).start();
            }
        }
        return this;
    }

    public Promise<T> then(Function<T> res) {
        synchronized (thenList) {
            if (state == STATE_RUNNING) {
                thenList.add(res);
            } else if (state == STATE_DONE) {
                new Task(()-> res.call(result)).start();
            }
        }
        return this;
    }

    public T getResult() {
        if (state == STATE_RUNNING) {
            thread.waitFor();
        }
        return result;
    }

    public interface PromiseRunnable<T> {
        T execute() throws Throwable;
    }
}
