package br.nullexcept.mux.app;

import br.nullexcept.mux.core.texel.TexelAPI;
import br.nullexcept.mux.lang.Valuable;
import br.nullexcept.mux.res.AssetsManager;
import br.nullexcept.mux.res.LayoutInflater;
import br.nullexcept.mux.res.Resources;

import java.util.HashMap;

public class Context {
    public static final String CLIPBOARD_APPLET = "applet.os.clipboard";

    private final Resources mResource;
    private final HashMap<String, Applet> applets = new HashMap<>();

    public Context(){
        mResource = new Resources(this);
        applets.putAll(TexelAPI.obtainApplets());
    }

    public <T extends Service> T startService(Class<T> clazz) {
        return startService(clazz, ()-> {
            try {
                return clazz.newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        });
    }

    public <T extends Service> T startService(Class<T> clazz, Valuable<T> service) {
        return Application.beginService(clazz, service);
    }

    public String getString(String id) {
        return mResource.getString(id);
    }

    public <T extends Applet> T getApplet(String name) {
        return (T) applets.get(name);
    }

    public LayoutInflater getLayoutInflater(){
        return mResource.getInflater();
    }

    public AssetsManager getAssetsManager() {
        return getResources().getAssetsManager();
    }

    public Resources getResources() {
        return mResource;
    }
}
