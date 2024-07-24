package br.nullexcept.mux.app;

import br.nullexcept.mux.lang.Parcel;
import br.nullexcept.mux.res.AssetsManager;
import br.nullexcept.mux.res.LayoutInflater;
import br.nullexcept.mux.res.Resources;

import java.io.File;

public class Context {
    public static final String CLIPBOARD_APPLET = "applet.os.clipboard";
    public static final String DISPLAY_APPLET = "applet.os.display";

    ApplicationRuntime appRuntime;
    Launch _args;

    public Context(){
    }

    public File getFilesDir() {
        return appRuntime.getFilesDir();
    }

    public ApplicationRuntime getApplication() {
        return appRuntime;
    }

    protected void onParcelChanged(Parcel parcel) {}

    protected Parcel getParcel() {
        return _args;
    }

    public <T extends Service> T startService(Launch<T> service) {
        return appRuntime.beginService(service);
    }

    public String getString(String id) {
        return getResources().getString(id);
    }

    public <T extends Applet> T getApplet(String name) {
        return appRuntime.getApplet(name);
    }

    public LayoutInflater getLayoutInflater(){
        return getResources().getInflater();
    }

    public AssetsManager getAssetsManager() {
        return getResources().getAssetsManager();
    }

    public Resources getResources() {
        return appRuntime.getResources();
    }
}
