package br.nullexcept.mux.res;

import java.io.InputStream;

public class AssetsManager {
    /** @TODO: NEED FIX FOR NATIVE COMPILE */
    public static InputStream openDocument(String dir){
        return AssetsManager.class.getResourceAsStream("/assets/"+dir);
    }
}
