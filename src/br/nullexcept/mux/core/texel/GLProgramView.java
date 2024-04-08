package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.utils.BufferUtils;

class GLProgramView extends GLProgram {
    private final String fragment;
    private final String vertex;

    GLProgramView(){
        fragment = BufferUtils.utfFromStream(getClass().getResourceAsStream("/res/raw/core_native_view.frag"));
        vertex = BufferUtils.utfFromStream(getClass().getResourceAsStream("/res/raw/core_native_view.vert"));
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