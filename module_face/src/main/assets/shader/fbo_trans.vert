// vertex shader
precision mediump float;
attribute vec4 aPosition;
uniform mat4 vMatrix;

attribute vec2 aTextureCoordinate;
varying vec2 vTextureCoordinate;

void main() {
    vTextureCoordinate = aTextureCoordinate;
    gl_Position = vMatrix*aPosition;
}
