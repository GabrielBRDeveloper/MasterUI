precision mediump float;

#define ALPHA float(params[1])/255.0
#define MODE params[0]

uniform int[8] params;

uniform sampler2D texture;

varying vec2 uv;

void main() {
    vec4 diffuse = vec4(1.0);

    if(MODE == 0) {
        diffuse = texture2D(texture, uv);
    }

    if(diffuse.a <= 0.0)
        discard;

    gl_FragColor = diffuse;
}