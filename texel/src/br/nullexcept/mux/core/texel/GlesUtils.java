package br.nullexcept.mux.core.texel;

import static br.nullexcept.mux.core.texel.GLES.*;

class GlesUtils {
    public static int compileShader(String source, int type){
        int shader = glCreateShader(type);
        glShaderSource(shader, source);
        glCompileShader(shader);
        int[] res = new int[1];
        glGetShaderiv(shader, GL_COMPILE_STATUS,res);
        if (res[0] != GL_TRUE){
            throw new RuntimeException("Error on compile gl shader: "+ glGetShaderInfoLog(shader)+"\nSource: \n"+source);
        }
        return shader;
    }
}
