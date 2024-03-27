package br.nullexcept.mux.core.texel;

class GLProgramView extends GLProgram {
    private final String fragment;
    private final String vertex;

    GLProgramView(){
        fragment =
                "precision mediump float;\n" +
                "\n" +
                "uniform float alpha;\n" +
                "uniform float time;\n" +
                "uniform sampler2D __texture__;\n" +
                "varying vec2 xuv;\n" +
                "\n" +
                "void main(){\n" +
                "    vec4 pixel = texture2D(__texture__, xuv);\n" +
                "    pixel.a *= alpha;\n" +
                "    if(pixel.a < 0.001) discard; " +
                "    gl_FragColor = pixel;\n" +
                "}";
        vertex =
                "attribute vec4 __position__;\n" +
                "attribute vec2 __uv__;\n" +
                "\n" +
                "varying vec2 xuv;\n" +
                "\n" +
                "void main(){\n" +
                "    gl_Position = vec4(__position__.xy,0.0, 1.0);\n" +
                "    xuv = __uv__ ;\n" +
                "}";
    }

    @Override
    protected String fragment() {
        return fragment.replaceAll("__texture__", UNIFORM_TEXTURE);
    }

    @Override
    protected String vertex() {
        return vertex.replaceAll("__position__", ATTRIBUTE_POSITION)
                .replaceAll("__uv__", ATTRIBUTE_UV);
    }

    @Override
    protected void preload(int program) {
    }
}