package br.nullexcept.mux.res;

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
        return clazz.getResourceAsStream((prefix+"/"+dir).replaceAll("//","/"));
    }

    public boolean exists(String dir) {
        try {
            InputStream input = openDocument(dir);
            if (input.read() != -1){
                input.close();
                return true;
            }
            input.close();
            return false;
        } catch (Exception e){
            return false;
        }
    }
}
