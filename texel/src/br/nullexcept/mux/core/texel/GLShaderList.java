package br.nullexcept.mux.core.texel;

import java.util.ArrayList;

class GLShaderList {
    private static ArrayList<GLProgram> list = new ArrayList<>();

    public static final GLProgram TEXTURE = register(new GLProgramTexture());
    public static final GLProgramView VIEW = register(new GLProgramView());

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
