precision mediump float;

attribute vec4 vPos;
attribute vec2 vTexCoords;

varying vec2 uv;

void main() {
    gl_Position = vec4(vPos.xy, 0.0, 1.0);
    uv = vTexCoords;
}