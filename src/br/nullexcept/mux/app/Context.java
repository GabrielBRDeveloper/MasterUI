package br.nullexcept.mux.app;

import br.nullexcept.mux.res.AssetsManager;
import br.nullexcept.mux.res.LayoutInflater;
import br.nullexcept.mux.res.Resources;

public class Context {
    private final Resources mResource;
    public Context(){
        mResource = new Resources(this);
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
