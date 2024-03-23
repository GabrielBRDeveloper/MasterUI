package br.nullexcept.mux.res;

import java.io.InputStream;

public class AssetsManager {
    private final String prefix;

    AssetsManager(String prefix) {
        this.prefix = prefix;
    }

    /** @TODO: NEED FIX FOR NATIVE COMPILE */
    public InputStream openDocument(String dir){
        return AssetsManager.class.getResourceAsStream((prefix+"/"+dir).replaceAll("//","/"));
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
