package br.nullexcept.mux.app;

public interface Project {
    String getPackage();
    Launch<Activity> getLaunch();

    CoreBoostrap getCoreBootstrap();
}