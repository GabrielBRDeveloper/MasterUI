package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.app.Activity;
import br.nullexcept.mux.app.CoreBoostrap;
import br.nullexcept.mux.app.Launch;
import br.nullexcept.mux.app.Project;

public class TexelProject implements Project {
    private final String package_;
    private final Launch<Activity> initial;
    private TexelAPI core = new TexelAPI();

    public <T extends Activity> TexelProject(String package_, Launch<T> initial) {
        this.package_ = package_;
        this.initial = (Launch<Activity>) initial;
    }

    @Override
    public String getPackage() {
        return package_;
    }

    @Override
    public Launch<Activity> getLaunch() {
        return initial;
    }

    @Override
    public CoreBoostrap getCoreBootstrap() {
        return core;
    }
}
