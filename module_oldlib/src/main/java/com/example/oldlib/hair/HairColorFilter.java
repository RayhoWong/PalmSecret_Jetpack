package com.example.oldlib.hair;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLES20;


import com.example.oldlib.old.GPUImageFilter;
import com.example.oldlib.old.OpenGlUtils;

import java.nio.FloatBuffer;

public class HairColorFilter extends GPUImageFilter {

    private GPUImageSharpenFilter mSharpenFilter;
    private GPUImageSoftLightColorBlendFilter mColorBlendFilter;
    private MaskColorBlendFilter mMaskBlendFilter;

    private int[] mFrameBuffer1 = new int[] {-1};
    private int[] mTexture1 = new int[] {-1};
    private int[] mFrameBuffer2 = new int[] {-1};
    private int[] mTexture2 = new int[] {-1};

    public HairColorFilter() {
        super();
        mSharpenFilter = new GPUImageSharpenFilter(0.3f);
        mColorBlendFilter = new GPUImageSoftLightColorBlendFilter();
        mMaskBlendFilter = new MaskColorBlendFilter();
    }

    public void setBlendColor(final int color) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                int red = Color.red(color);
                int green = Color.green(color);
                int blue = Color.blue(color);
                mColorBlendFilter.setOverlay(new float[] {(float)red / 255.0f, (float)green / 255.0f, (float)blue / 255.0f, 1.0f});
            }
        });
    }

    public void setMaskBitmap(Bitmap maskBitmap) {
        mMaskBlendFilter.setMaskBitmap(maskBitmap);
    }

    @Override
    public void onInit() {
        super.onInit();
        mSharpenFilter.init();
        mColorBlendFilter.init();
        mMaskBlendFilter.init();
    }

    @Override
    public void onOutputSizeChanged(int width, int height) {
        super.onOutputSizeChanged(width, height);
        mSharpenFilter.onOutputSizeChanged(width, height);
        mColorBlendFilter.onOutputSizeChanged(width, height);
        mMaskBlendFilter.onOutputSizeChanged(width, height);
        GLES20.glViewport(0, 0, width, height);

        initFrameBuffers(width, height);
    }

    @Override
    public void onDraw(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
        GLES20.glViewport(0, 0, mOutputWidth, mOutputHeight);
        super.onDraw(textureId, cubeBuffer, textureBuffer);
        onDrawToFrameBuffer(textureId, 0, true);
    }

    public void onDrawToFrameBuffer(int textureId, int framebuffer, boolean isFilp) {
//        Bitmap bitmap = Bitmap.createBitmap(getOutputWidth(), getOutputHeight(), Bitmap.Config.ARGB_8888);
//        IntBuffer ib = IntBuffer.allocate(mOutputWidth * mOutputHeight);

        runPendingOnDrawTasks();

        // 锐化处理原图
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffer1[0]);
        mSharpenFilter.onDraw(textureId, mGLCubeBuffer, mGLTextureFlipBuffer);

//        ib.position(0);
//        GLES20.glReadPixels(0, 0, mOutputWidth, mOutputHeight, GL_RGBA, GL_UNSIGNED_BYTE, ib);
//        bitmap.copyPixelsFromBuffer(ib);

        // 颜色混合
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffer2[0]);
        mColorBlendFilter.onDraw(mTexture1[0], mGLCubeBuffer, mGLTextureFlipBuffer);
//        ib.position(0);
//        GLES20.glReadPixels(0, 0, mOutputWidth, mOutputHeight, GL_RGBA, GL_UNSIGNED_BYTE, ib);
//        bitmap.copyPixelsFromBuffer(ib);

        // 掩码混合
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffer1[0]);
        mMaskBlendFilter.setModifiedImageTexture(mTexture2[0]);
        mMaskBlendFilter.onDraw(textureId, mGLCubeBuffer, mGLTextureFlipBuffer);
//        ib.position(0);
//        GLES20.glReadPixels(0, 0, mOutputWidth, mOutputHeight, GL_RGBA, GL_UNSIGNED_BYTE, ib);
//        bitmap.copyPixelsFromBuffer(ib);
        // 最终输出
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer);
        GLES20.glViewport(0, 0, mOutputWidth, mOutputHeight);
        super.onDraw(mTexture1[0], mGLCubeBuffer, framebuffer == 0 ? mGLTextureBuffer : mGLTextureFlipBuffer);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
//        ib.position(0);
//        GLES20.glReadPixels(0, 0, mOutputWidth, mOutputHeight, GL_RGBA, GL_UNSIGNED_BYTE, ib);
//        bitmap.copyPixelsFromBuffer(ib);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSharpenFilter.destroy();
        mColorBlendFilter.destroy();
        mMaskBlendFilter.destroy();

        destroyFrameBuffers();
    }

    private void initFrameBuffers(int width, int height) {
        OpenGlUtils.bindFrameBufferToTexture(width, height, mFrameBuffer1, mTexture1);
        OpenGlUtils.bindFrameBufferToTexture(width, height, mFrameBuffer2, mTexture2);
    }

    private void destroyFrameBuffers() {
        if (mFrameBuffer1 != null && mFrameBuffer1[0] != OpenGlUtils.NO_TEXTURE) {
            GLES20.glDeleteFramebuffers(mFrameBuffer1.length, mFrameBuffer1, 0);
            mFrameBuffer1[0] = OpenGlUtils.NO_TEXTURE;
        }
        if (mTexture1 != null && mTexture1[0] != OpenGlUtils.NO_TEXTURE) {
            GLES20.glDeleteTextures(mTexture1.length, mTexture1, 0);
            mTexture1[0] = OpenGlUtils.NO_TEXTURE;
        }
        if (mFrameBuffer2 != null && mFrameBuffer2[0] != OpenGlUtils.NO_TEXTURE) {
            GLES20.glDeleteFramebuffers(mFrameBuffer2.length, mFrameBuffer2, 0);
            mFrameBuffer2[0] = OpenGlUtils.NO_TEXTURE;
        }
        if (mTexture2 != null && mTexture2[0] != OpenGlUtils.NO_TEXTURE) {
            GLES20.glDeleteTextures(mTexture2.length, mTexture2, 0);
            mTexture2[0] = OpenGlUtils.NO_TEXTURE;
        }
    }
}
