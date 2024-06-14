package br.nullexcept.mux.app;

public interface Project {
    String getPackage();
    Launch<Activity> getLaunch();

    static <T extends Activity> Project build(Launch<T> init, String package_) {
        return new SimpleProject(package_, init);
    }
}

class SimpleProject  <T extends Activity> implements Project {
    private final String package_;
    private final Launch<T> initial;

    SimpleProject(String package_, Launch<T> initial) {
        this.package_ = package_;
        this.initial = initial;
    }

    @Override
    public String getPackage() {
        return package_;
    }

    @Override
    public Launch<Activity> getLaunch() {
        return (Launch<Activity>) initial;
    }
}