package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.lang.Bindable;
import br.nullexcept.mux.lang.Disposable;

import static br.nullexcept.mux.hardware.GLES.*;

abstract class GLProgram implements Bindable, Disposable {

    public static final String ATTRIBUTE_POSITION = "vPosition";
    public static final String ATTRIBUTE_UV = "vTextureCoords";
    public static final String UNIFORM_TEXTURE = "uTexture";

    private int program = -1;
    private int vertexShader;
    private int fragShader;
    protected abstract String fragment();
    protected abstract String vertex();

    public void build(){
        program = glCreateProgram();
        vertexShader = GlesUtils.compileShader(vertex(), GL_VERTEX_SHADER);
        fragShader = GlesUtils.compileShader(fragment(), GL_FRAGMENT_SHADER);

        glAttachShader(program, vertexShader);
        glAttachShader(program, fragShader);
        preload(program);
        glLinkProgram(program);
        glUseProgram(0);
    }

    protected abstract void preload(int program);

    public void dispose(){
        glDeleteProgram(program);
        glDeleteShader(vertexShader);
        glDeleteShader(fragShader);
    }

    public int attribute(String var){
        return glGetAttribLocation(program, var);
    }

    public int uniform(String var){
        return glGetUniformLocation(program, var);
    };

    public void bind(){
        if (program == -1)
            throw new RuntimeException("Shader not complied");
        glUseProgram(program);
    }

    @Override
    public void unbind() {
        glUseProgram(0);
    }
}
