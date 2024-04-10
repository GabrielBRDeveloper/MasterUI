precision mediump float;

#define ALPHA float(params[1])/255.0
#define MODE params[0]

uniform int[8] params;

uniform sampler2D texture;

varying vec2 uv;

void main() {
    vec4 color = texture2D(texture, uv);
    color.a *= ALPHA;
    if(color.a <= 0.0) discard;

    if(MODE == 0) {
        color.rgb *= color.a;
    } else if(ALPHA == 1.0){
        if(color.a >= 1.0)discard;
    } else {
        discard;
    }

    gl_FragColor = color;
}