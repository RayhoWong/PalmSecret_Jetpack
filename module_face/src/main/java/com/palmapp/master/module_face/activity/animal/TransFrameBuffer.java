package com.palmapp.master.module_face.activity.animal;

import android.content.Context;

/**
 * @author rayhahah
 * @blog http://rayhahah.com
 * @time 2019/9/24
 * @tips 这个类是Object的子类
 * @fuction
 */
public class TransFrameBuffer extends AbsFrameBuffer {

    private float[] mSrcMeshPoints;
    private float[] mTargetMeshPoints;
    private float[] mCurrentMeshPoints;

    private float[] mSrcControlPoints;
    private int mAreaCount;

    public TransFrameBuffer(Context context) {
        super(context,
                OpenGLUtils.getShaderFromAssets(context, "shader/fbo_trans.vert"),
                OpenGLUtils.getShaderFromAssets(context, "shader/fbo_trans.frag"));
    }

    @Override
    protected void initProgramHandle(int programHandle) {
        super.initProgramHandle(programHandle);
    }

    @Override
    protected void onDrawFrame() {
        super.onDrawFrame();
    }

    public void setSrcMesh(float[] srcMeshPoints, int width, int height) {
        mSrcMeshPoints = srcMeshPoints;
        mImageWidth = width;
        mImageHeight = height;
        float[] glPos = OpenGLUtils.convertMeshPosToGLPos(srcMeshPoints, width, height, 1);
        float[] textureData = OpenGLUtils.rotateTextureData(
                OpenGLUtils.convertGLPosToTrianglePos(glPos, mAreaCount, mAreaCount)
        );
        mTextureBuffer = OpenGLUtils.createFloatBuffer(textureData);
    }

    public float[] getSrcMeshPoints() {
        return mSrcMeshPoints;
    }

    public float[] getTargetMeshPoints() {
        return mTargetMeshPoints;
    }

    public void setTargetMeshPoints(float[] targetMeshPoints) {
        this.mTargetMeshPoints = targetMeshPoints;
    }

    public float[] getSrcControlPoints() {
        return mSrcControlPoints;
    }

    public void setSrcControlPoints(float[] srcControlPoints) {
        this.mSrcControlPoints = srcControlPoints;
    }

    public void setAreaCount(int areaCount) {
        mAreaCount = areaCount;
    }


    public float[] getCurrentMeshPoints() {
        return mCurrentMeshPoints;
    }

    public void setCurrentMeshPoints(float[] currentMeshPoints) {
        mCurrentMeshPoints = currentMeshPoints;
    }

    public void setVertex() {
        float[] glPos = OpenGLUtils.convertMeshPosToGLPos(mCurrentMeshPoints, mImageWidth, mImageHeight, 0);
        float[] vertexData = OpenGLUtils.convertGLPosToTrianglePos(glPos, mAreaCount, mAreaCount);
        mVertexBuffer = OpenGLUtils.createFloatBuffer(vertexData);
        mVertexCount = vertexData.length / mCoordsPerVertex;
    }
}
