precision mediump float;

#define ALPHA float(params[1])/255.0

uniform int[8] params;

uniform sampler2D texture;

varying vec2 uv;

void main() {
    vec4 color = texture2D(texture, uv);

    gl_FragColor = color;
}