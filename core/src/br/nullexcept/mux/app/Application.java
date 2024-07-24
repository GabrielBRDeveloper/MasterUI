package br.nullexcept.mux.app;

import br.nullexcept.mux.app.base.Project;

public class Application {
    public static void initialize(Project project){
        new Thread(()->{
            new ApplicationRuntime(project).start();
        }).start();
    }
}
