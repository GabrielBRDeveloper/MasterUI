attribute vec4 __position__;
attribute vec2 __uv__;

varying vec2 xuv;

void main(){
    gl_Position = vec4(__position__.xy,0.0, 1.0);
    xuv = __uv__ ;
}
