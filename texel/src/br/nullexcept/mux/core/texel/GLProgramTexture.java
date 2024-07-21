package br.nullexcept.mux.core.texel;

class GLProgramTexture extends GLProgram {
    @Override
    protected String fragment() {
        return ("precision mediump float;\n" +
                "uniform sampler2D __texture__;\n" +
                "varying vec2 xuv\n\n;" +
                "void main(){\n" +
                "   gl_FragColor = texture2D(__texture__, xuv);\n" +
                "}\n").replaceAll("__texture__", UNIFORM_TEXTURE)
                .replaceAll("\n","\n\r");
    }

    @Override
    protected String vertex() {
        return ("precision mediump float;\n" +
                "attribute vec4 __position__;\n" +
                "attribute vec2 __uv__;\n" +
                "varying vec2 xuv;\n\n" +
                "void main(){\n" +
                "   gl_Position = __position__;\n" +
                "   xuv = __uv__;\n" +
                "}\n").replaceAll("__position__", ATTRIBUTE_POSITION)
                .replaceAll("__uv__", ATTRIBUTE_UV)
                .replaceAll("\n","\n\r");
    }

    @Override
    protected void preload(int program) {
    }
}