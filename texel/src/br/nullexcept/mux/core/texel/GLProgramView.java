package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.utils.BufferUtils;

class GLProgramView extends GLProgram {
    public int texture;
    public int position;
    public int uv;
    public int params;

    private final String fragment;
    private final String vertex;

    GLProgramView(){
        fragment = BufferUtils.utfFromStream(getClass().getResourceAsStream("/res/raw/view_renderer.frag"));
        vertex = BufferUtils.utfFromStream(getClass().getResourceAsStream("/res/raw/view_renderer.vert"));
    }

    @Override
    public void build() {
        super.build();

        bind();
        position = GLES.glGetAttribLocation(id(), "vPos");
        uv = GLES.glGetAttribLocation(id(), "vTexCoords");
        texture = GLES.glGetUniformLocation(id(), "texture");
        params = GLES.glGetUniformLocation(id(), "params");

        unbind();
    }

    @Override
    protected void preload(int program) {

    }

    @Override
    protected String fragment() {
        return fragment;
    }

    @Override
    protected String vertex() {
        return vertex;
    }

    @Override
    public String toString() {
        return "GLProgramView{" +
                "texture=" + texture +
                ", position=" + position +
                ", uv=" + uv+
                '}';
    }
}