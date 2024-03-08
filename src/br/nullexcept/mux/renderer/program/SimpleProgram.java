package br.nullexcept.mux.renderer.program;

class SimpleProgram extends GLProgram {
    @Override
    protected String fragment() {
        return ("precision mediump float;\n" +
                "uniform vec4 __color__;\n" +
                "void main(){\n" +
                "   gl_FragColor = __color__;\n" +
                "};\n").replaceAll("__color__", ATTRIBUTE_COLOR);
    }

    @Override
    protected String vertex() {
        return ("attribute vec4 __position__;\n" +
                "void main(){\n" +
                "   gl_Position = __position__;\n" +
                "};\n").replaceAll("__position__", ATTRIBUTE_POSITION);
    }

    @Override
    protected void preload(int program) {
    }
}
