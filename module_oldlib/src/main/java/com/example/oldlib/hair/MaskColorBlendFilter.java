package com.example.oldlib.hair;

import android.graphics.Bitmap;
import android.opengl.GLES20;

import com.example.oldlib.old.GPUImageFilter;
import com.example.oldlib.old.OpenGlUtils;


public class MaskColorBlendFilter extends GPUImageFilter {

    private int mInputTexture2Handle;
    private int mInputTexture3Handle;
    private int mMaskTextureId = OpenGlUtils.NO_TEXTURE;
    private int mModifiedImageTextureId = OpenGlUtils.NO_TEXTURE;
    private Bitmap mMaskBitmap;

    public MaskColorBlendFilter() {
        super(
                "attribute vec4 position;" +
                        "\nattribute vec4 inputTextureCoordinate;\n" +
                        " \nvarying vec2 textureCoordinate;\n" +
                        " \nvoid main()\n{\n    " +
                        "gl_Position = position;\n  " +
                        "  textureCoordinate = inputTextureCoordinate.xy;\n}",

                "varying highp vec2 textureCoordinate;\n " +
                        "\n uniform sampler2D inputImageTexture;\n " +// src
                        "uniform sampler2D inputImageTexture2; \n" +  // texture
                        " uniform sampler2D inputImageTexture3; \n" + // mask
                        " \n void main()\n {\n  " +
                        "   lowp vec4 originImageColor = texture2D(inputImageTexture, textureCoordinate);\n  " +
                        "   lowp vec4 modifiedImageColor = texture2D(inputImageTexture2, textureCoordinate);\n   " +
                        "  lowp vec4 select = texture2D(inputImageTexture3, textureCoordinate);\n" +
                        "     \n  " +
                        "   gl_FragColor = mix(originImageColor, vec4(0.0, 0.0, 0.0, 0.0), select.r);\n " +
                        "}\n"
        );
    }

    @Override
    public void onInit() {
        super.onInit();
        mInputTexture2Handle = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture2");
        mInputTexture3Handle = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture3");
        if (mMaskBitmap != null && !mMaskBitmap.isRecycled()) {
            setMaskBitmap(mMaskBitmap);
        }
    }

    public void setMaskBitmap(final Bitmap bitmap) {
        if (bitmap == null || !bitmap.isRecycled()) {
            mMaskBitmap = bitmap;
            if (mMaskBitmap != null) {
                runOnDraw(new Runnable() {
                    @Override
                    public void run() {
                        if (mMaskTextureId == OpenGlUtils.NO_TEXTURE) {
                            GLES20.glActiveTexture(GLES20.GL_TEXTURE5);
                            mMaskTextureId = OpenGlUtils.loadTexture(bitmap, mMaskTextureId, false);
                        }
                    }
                });
            }
        }
    }

    public void setModifiedImageTexture(final int textureId) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                if (mModifiedImageTextureId == OpenGlUtils.NO_TEXTURE) {
                    mModifiedImageTextureId = textureId;
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GLES20.glDeleteTextures(1, new int[]{mMaskTextureId}, 0);
        mMaskTextureId = OpenGlUtils.NO_TEXTURE;
        mMaskBitmap = null;
    }

    @Override
    protected void onDrawArraysPre() {
        super.onDrawArraysPre();
        GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mModifiedImageTextureId);
        GLES20.glUniform1i(mInputTexture2Handle, 3);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE5);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mMaskTextureId);
        GLES20.glUniform1i(mInputTexture3Handle, 5);
    }
}
