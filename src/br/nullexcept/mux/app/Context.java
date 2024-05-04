package br.nullexcept.mux.app;

import br.nullexcept.mux.core.texel.TexelAPI;
import br.nullexcept.mux.lang.Parcel;
import br.nullexcept.mux.res.AssetsManager;
import br.nullexcept.mux.res.LayoutInflater;
import br.nullexcept.mux.res.Resources;

import java.util.HashMap;

public class Context {
    public static final String CLIPBOARD_APPLET = "applet.os.clipboard";

    private final Resources mResource;
    private final HashMap<String, Applet> applets = new HashMap<>();
    Launch _args;

    public Context(){
        mResource = new Resources(this);
        applets.putAll(TexelAPI.obtainApplets());
    }

    protected void onParcelChanged(Parcel parcel) {}

    protected Parcel getParcel() {
        return _args;
    }

    public <T extends Service> T startService(Launch<T> service) {
        return Application.beginService(service);
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
