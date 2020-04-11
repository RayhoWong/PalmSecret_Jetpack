package com.example.oldlib.hair;

import android.opengl.GLES20;


import com.example.oldlib.old.GPUImageFilter;
import com.example.oldlib.old.OpenGlUtils;

import java.nio.FloatBuffer;

public class GPUImageSoftLightColorBlendFilter extends GPUImageFilter {
    public static final String SOFT_LIGHT_BLEND_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\n" +
            "\n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform mediump vec4 overlay;\n" +
//            " const highp vec4 overlay = vec4(1.0, 0, 0, 1.0);\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     mediump vec4 base = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     \n" +
            "     gl_FragColor = vec4(base.rgb * (overlay.a * (base.rgb / base.a) + (2.0 * overlay.rgb * (1.0 - (base.rgb / base.a)))) + overlay.rgb * (1.0 - base.a) + base.rgb * (1.0 - overlay.a), base.a);\n" +
            " }";

//    public static final String SOFT_LIGHT_BLEND_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\n" +
//            "\n" +
//            " uniform sampler2D inputImageTexture;\n" +
//            " uniform mediump vec4 overlay;\n" +
////            " const highp vec4 overlay = vec4(1.0, 0, 0, 1.0);\n" +
//            " \n" +
//            " void main()\n" +
//            " {\n" +
//            "     mediump vec4 base = texture2D(inputImageTexture, textureCoordinate);\n" +
//            "     mediump float colorR;\n" +
//            "     mediump float colorG;\n" +
//            "     mediump float colorB;\n" +
//            "     mediump float colorA;\n" +
//            "     if(overlay.r < 0.5) {\n" +
//            "           colorR = base.r * overlay.r * 2.0 + base.r * base.r * (1.0- 2.0 * overlay.r);\n" +
//            "     } else {\n" +
//            "           colorR = 2.0 * base.r * (1.0 - overlay.r) + sqrt(base.r) * (2.0 * overlay.r - 1.0);\n" +
//            "     }\n" +
//            "     if(overlay.g < 0.5) {\n" +
//            "           colorG = base.g * overlay.g * 2.0 + base.g * base.g * (1.0- 2.0 * overlay.g);\n" +
//            "     } else {\n" +
//            "           colorG = 2.0 * base.g * (1.0 - overlay.g) + sqrt(base.g) * (2.0 * overlay.g - 1.0);\n" +
//            "     }\n" +
//            "     if(overlay.b < 0.5) {\n" +
//            "           colorB = base.b * overlay.b * 2.0 + base.b * base.b * (1.0- 2.0 * overlay.b);\n" +
//            "     } else {\n" +
//            "           colorB = 2.0 * base.b * (1.0 - overlay.b) + sqrt(base.b) * (2.0 * overlay.b - 1.0);\n" +
//            "     }\n" +
//            "     gl_FragColor = vec4(colorR, colorG, colorB, base.a);\n" +
//            " }";

    private float[] mOverlay;
    private int mOverlayLocation;

    public GPUImageSoftLightColorBlendFilter() {
        this(new float[]{1.0f, 0f, 0f, 1f});
    }

    public GPUImageSoftLightColorBlendFilter(float[] rgba) {
        super(NO_FILTER_VERTEX_SHADER, SOFT_LIGHT_BLEND_FRAGMENT_SHADER);
        this.mOverlay = rgba;
    }

    @Override
    public void onInit() {
        super.onInit();
        mOverlayLocation = GLES20.glGetUniformLocation(getProgram(), "overlay");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setOverlay(mOverlay);
    }

    public void onDraw(final int textureId, final FloatBuffer cubeBuffer,
                       final FloatBuffer textureBuffer) {
        GLES20.glUseProgram(mGLProgId);
        runPendingOnDrawTasks();
        if (!isInitialized()) {
            return;
        }

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        cubeBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttribPosition, 2, GLES20.GL_FLOAT, false, 0, cubeBuffer);
        GLES20.glEnableVertexAttribArray(mGLAttribPosition);
        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0,
                textureBuffer);
        GLES20.glEnableVertexAttribArray(mGLAttribTextureCoordinate);
        if (textureId != OpenGlUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glUniform1i(mGLUniformTexture, 0);
        }
        onDrawArraysPre();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(mGLAttribPosition);
        GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glDisable(GLES20.GL_BLEND);
    }

    public void setOverlay(float[] overlay) {
        this.mOverlay = overlay;
        setFloatVec4(mOverlayLocation, overlay);
    }
}
