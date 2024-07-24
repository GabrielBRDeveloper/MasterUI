package br.nullexcept.mux.app;

import br.nullexcept.mux.app.base.Project;

public class Application {
    public static ApplicationRuntime initialize(Project project){
        ApplicationRuntime[] app = new ApplicationRuntime[1];
        new Thread(()->{
            app[0] = new ApplicationRuntime(project);
            app[0].start();
        }).start();
        while (app[0] == null) {Looper.sleep(0,100);}
        return app[0];
    }
}
