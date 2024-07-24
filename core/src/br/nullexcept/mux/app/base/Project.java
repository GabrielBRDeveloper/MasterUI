package br.nullexcept.mux.app.base;

import br.nullexcept.mux.app.Activity;
import br.nullexcept.mux.app.Launch;
import br.nullexcept.mux.app.base.CoreBoostrap;

public interface Project {
    String getPackage();
    Launch<Activity> getLaunch();

    CoreBoostrap getCoreBootstrap();
}