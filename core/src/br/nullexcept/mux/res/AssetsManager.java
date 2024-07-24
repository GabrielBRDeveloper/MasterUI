package br.nullexcept.mux.res;

import br.nullexcept.mux.C;
import br.nullexcept.mux.lang.ValuedFunction;

import java.io.InputStream;

public class AssetsManager {
    private final String prefix;
    private final Class clazz;

    AssetsManager(String prefix, Class clazz) {
        this.prefix = prefix;
        this.clazz = clazz;
    }

    /** @TODO: NEED FIX FOR NATIVE COMPILE */
    public InputStream openDocument(String dir){
        String abs = (prefix+"/"+dir).replaceAll("//","/");
        InputStream stream;
        for (AssetsLoader loader : C.ASSETS_LOADER) {
            try {
                if ((stream = loader.open(clazz, abs)) != null) {
                    return stream;
                }
            } catch (Exception e){}
        }
        return null;
    }

    public boolean exists(String dir) {
        String abs = (prefix+"/"+dir).replaceAll("//","/");
        for (AssetsLoader loader: C.ASSETS_LOADER) {
            if (loader.exists(clazz, abs)) {
                return true;
            }
        }
        return false;
    }

    public interface AssetsLoader {
        InputStream open(Class loader, String directory) throws Exception;
        boolean exists(Class loader, String directory);
    }
}
