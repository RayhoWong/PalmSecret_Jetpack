package com.example.oldlib.hair;

import android.opengl.GLES20;

public class GPUImageIntensityBlendFilter extends GPUImageTwoInputFilter {
    public static final float AGE_50 = 0.4f;
    public static final float AGE_70 = 0.7f;
    public static final float AGE_90 = 1.0f;

    private float mIntentsity;
    private int mIntentsityLocation;

    public static final String FRAGMENT_SHADER =
            "    precision mediump float;\n" +
                    "    varying highp vec2 textureCoordinate;\n" +
                    "    varying highp vec2 textureCoordinate2; // TODO: This is not used\n" +
                    "\n" +
                    "    uniform sampler2D inputImageTexture;\n" +
                    "    uniform sampler2D inputImageTexture2;\n" +
                    "    uniform lowp float intensity;\n" +
                    "    void main()\n" +
                    "    {\n" +
                    "        mediump vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                    "        mediump vec4 textureColor2 = texture2D(inputImageTexture2, textureCoordinate2);\n" +
                    "        gl_FragColor = vec4(mix(textureColor, textureColor2, 1. - intensity).rgb, 1.0);\n" +
                    "    }";


    public GPUImageIntensityBlendFilter() {
        super(FRAGMENT_SHADER);
        mIntentsity = 1.0f;
    }

    @Override
    public void onInit() {
        super.onInit();
        mIntentsityLocation = GLES20.glGetUniformLocation(getProgram(), "intensity");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setIntensity(mIntentsity);
    }

    @Override
    public boolean isSupportIntensity() {
        return true;
    }

    @Override
    public float getIntensity() {
        return mIntentsity;
    }

    public void setIntensity(float intensity) {
        mIntentsity = intensity;
        setFloat(mIntentsityLocation, intensity);
    }
}
