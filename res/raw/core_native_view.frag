precision highp float;

uniform float alpha;          // View.getAlpha()
uniform vec2  size;            // View.getWidth(), View.getHeight()

/** FOR SHADOW **/
uniform vec4  shadowColor;     // Shadow Color
uniform float elevation;      // View.getElevation()


/** GENERAL **/
uniform int mode;
uniform sampler2D __texture__;
varying vec2 xuv;

vec4 drawNormal() {
    vec4 pixel = texture2D(__texture__, xuv);
    return pixel;
}

vec4 blur(sampler2D image, vec2 uv, vec2 resolution, vec2 direction) {
    vec2 center = vec2(0.5, 0.5);
    vec4 color = vec4(0.0);
    vec2 us = gl_FragCoord.xy / resolution;
    float px = (1.0 / size.x);
    float py = (1.0 / size.y);

    int count = 1;
    for(float ix = -elevation; ix < elevation; ix++) {
        for(float iy = -elevation; iy < elevation; iy++) {
            color.a += texture2D(image, uv + vec2(ix*px, iy*py)).a;
            count++;
        }
    }

    color.a /= float(count);

    return color;
}

vec4 drawShadow() {
    vec4 color = blur(__texture__, xuv, size, vec2(elevation));
    color.a *= shadowColor.a;
    color.rgb = shadowColor.rgb;

    return color;
}

void main(){
    vec4 color = vec4(1.0);
    if(mode == 0) {
        color = drawNormal();
    } else if(mode == 1) {
        color = drawShadow();
    }

    color.a *= alpha;

    if(color.a <= 0.001)
        discard;

    gl_FragColor = color;
}