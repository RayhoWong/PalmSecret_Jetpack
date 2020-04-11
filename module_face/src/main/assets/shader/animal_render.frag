// fragment shader
precision mediump float;
varying vec2 vTextureCoordinate1;
varying vec2 vTextureCoordinate2;
uniform sampler2D uTexture1;
uniform sampler2D uTexture2;
uniform float uProgress;
void main() {
    vec4 color1=texture2D(uTexture1, vTextureCoordinate1);
    vec4 color2=texture2D(uTexture2, vTextureCoordinate2);
    vec4 mix12=mix(color1, color2, uProgress);
    gl_FragColor = mix12;
//    gl_FragColor = mix12;
    //    gl_FragColor = vec4(color3.rgb,1);
    //    gl_FragColor = color2;
    //    gl_FragColor = vec4(color1.rgb, 0.5)+ vec4(color2.rgb, 0.5);
}
