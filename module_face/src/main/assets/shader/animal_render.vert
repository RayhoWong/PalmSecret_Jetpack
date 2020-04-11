// vertex shader
precision mediump float;
attribute vec4 aPosition;
uniform mat4 vMatrix;

attribute vec2 aTextureCoordinate1;
varying vec2 vTextureCoordinate1;

attribute vec2 aTextureCoordinate2;
varying vec2 vTextureCoordinate2;

void main() {
    vTextureCoordinate1 = aTextureCoordinate1;
    vTextureCoordinate2 = aTextureCoordinate2;
    gl_Position = vMatrix*aPosition;
}