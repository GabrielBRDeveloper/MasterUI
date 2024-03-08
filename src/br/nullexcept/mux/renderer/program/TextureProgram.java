package br.nullexcept.mux.renderer.program;

class TextureProgram extends GLProgram {
    @Override
    protected String fragment() {
        return ("precision mediump float;\n" +
                "uniform sampler2D __texture__;\n" +
                "varying vec2 xuv;" +
                "void main(){\n" +
                "   vec4 pixel = texture2D(__texture__, xuv);\n" +
                "   gl_FragColor = pixel;\n" +
                "};\n").replaceAll("__texture__", UNIFORM_TEXTURE);
    }

    @Override
    protected String vertex() {
        return ("attribute vec4 __position__;\n" +
                "attribute vec2 __uv__;\n" +
                "varying vec2 xuv;" +
                "void main(){\n" +
                "   gl_Position = __position__;\n" +
                "   xuv = __uv__;\n" +
                "};\n").replaceAll("__position__", ATTRIBUTE_POSITION)
                .replaceAll("__uv__", ATTRIBUTE_UV);
    }

    @Override
    protected void preload(int program) {
    }
}