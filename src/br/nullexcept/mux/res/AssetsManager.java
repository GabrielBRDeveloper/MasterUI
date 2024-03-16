package br.nullexcept.mux.res;

import java.io.InputStream;

public class AssetsManager {
    /** @TODO: NEED FIX FOR NATIVE COMPILE */
    public static InputStream openDocument(String dir){
        return AssetsManager.class.getResourceAsStream(("/assets/"+dir).replaceAll("//","/"));
    }

    public static boolean exists(String dir) {
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
