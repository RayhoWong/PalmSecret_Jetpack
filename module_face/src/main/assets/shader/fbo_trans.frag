// fragment shader
precision mediump float;
varying vec2 vTextureCoordinate;
uniform sampler2D uTexture;
void main() {
    vec4 color=texture2D(uTexture, vTextureCoordinate);
    gl_FragColor = color;
}
