package br.nullexcept.mux.renderer.program;

import java.util.ArrayList;

public class GLShaderList {
    private static ArrayList<GLProgram> list = new ArrayList<>();

    public static final GLProgram BASIC = register(new SimpleProgram());
    public static final GLProgram TEXT = register(new TextProgram());
    public static final GLProgram TEXTURE = register(new TextureProgram());

    public static <T extends GLProgram> T register(T value){
        list.add(value);
        return value;
    }

    public static void build(){
        for (GLProgram program : list){
            program.build();
        }
    }

    public static void dispose(){
        for (GLProgram program: list)
            program.dispose();
    }
}
