package com.example.oldlib.old;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static javax.microedition.khronos.opengles.GL10.GL_RGBA;
import static javax.microedition.khronos.opengles.GL10.GL_UNSIGNED_BYTE;

@SuppressLint("WrongCall")
@TargetApi(11)
public class GPUImageRenderer implements GLSurfaceView.Renderer{
    private static final String TAG = "GPUImageRenderer";

    public static final int NO_IMAGE = -1;
    public static final float CUBE[] = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f,
    };

    private GPUImageFilter mFilter;

    public final Object mSurfaceChangedWaiter = new Object();

    protected int mGLTextureId = NO_IMAGE;
    private int mSurfaceTextureID = -1;
    protected SurfaceTexture mSurfaceTexture = null;
    private final FloatBuffer mGLCubeBuffer;
    private final FloatBuffer mGLTextureBuffer;
    private IntBuffer mGLRgbBuffer;

    private int mOutputWidth;
    private int mOutputHeight;
    private int mImageWidth = 0;
    private int mImageHeight = 0;
    private int mPreviewWidth = 0;
    private int mPreviewHeight = 0;
    private int mAddedPadding;

    private final Queue<Runnable> mRunOnDraw;
    private final Queue<Runnable> mRunOnDrawEnd;
    private Rotation mRotation;
    private boolean mFlipHorizontal;
    private boolean mFlipVertical;
    private ImageView.ScaleType mScaleType = ImageView.ScaleType.CENTER_CROP;

    private FiltFrameListener mListener;

    private IRenderCallback mFrameCallback;

    private boolean mSizeChanging = false;
    private boolean mRotationChanging = false;
    private boolean mFilterChanging = false;
    private Object mChangeingLock = new Object();

    private boolean mIsCamera = false;

    private int mRecordingRotation;
    private final FloatBuffer mVideoTextureBuffer;
    private Runnable mVideoStartRunnable;
    private boolean mIsRecording;

    //用于预览缓存
    protected byte mBuffer[];


    public static final float TEXTURE_NO_ROTATION[] = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
    };

    public GPUImageRenderer(final GPUImageFilter filter, IRenderCallback frameCallback, boolean isCamera) {
        mFilter = filter;
        mFrameCallback = frameCallback;
        mIsCamera = isCamera;
        mRunOnDraw = new LinkedList<Runnable>();
        mRunOnDrawEnd = new LinkedList<Runnable>();

        mGLCubeBuffer = ByteBuffer.allocateDirect(CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLCubeBuffer.put(CUBE).position(0);

        mGLTextureBuffer = ByteBuffer.allocateDirect(TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mVideoTextureBuffer = ByteBuffer.allocateDirect(TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        setRotation(Rotation.NORMAL, false, false);
    }

    @Override
    public void onSurfaceCreated(final GL10 unused, final EGLConfig config) {
//    	int color = Color.parseColor("#262827");
//    	float red = Color.red(color)*1.0f/255;
//    	float g = Color.green(color)*1.0f/255;
//    	float b = Color.blue(color)*1.0f/255;
        mSurfaceTextureID = createTextureID();
        mSurfaceTexture = new SurfaceTexture(mSurfaceTextureID);
//        mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
//            @Override
//            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
//                if (isOESFilter()) {
//                    mFilterChanging = false;
//                    if(mFrameCallback != null) {
//                        mFrameCallback.onFrameAvaliable(surfaceTexture.getTimestamp());
//                    }
//                }
//            }
//        });
        GLES20.glClearColor(0f, 0f, 0f, 1); //此背景与编辑界面背景色一致
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        mFilter.init();
        if(mFrameCallback != null) {
            mFrameCallback.onSurfaceTextureCreated(mSurfaceTexture);
        }
    }

    @Override
    public void onSurfaceChanged(final GL10 gl, final int width, final int height) {
        mOutputWidth = width;
        mOutputHeight = height;
        GLES20.glViewport(0, 0, width, height);
        GLES20.glUseProgram(mFilter.getProgram());
        mFilter.onOutputSizeChanged(width, height);
        adjustImageScaling();
        synchronized (mChangeingLock) {
            mSizeChanging = mIsCamera;
        }
        synchronized (mSurfaceChangedWaiter) {
            mSurfaceChangedWaiter.notifyAll();
        }
    }

    @Override
    public void onDrawFrame(final GL10 gl) {
        runAll(mRunOnDraw);
        synchronized (mChangeingLock) {
            if (mRotationChanging) {
                mRotationChanging = false;
                try {
                    if (mSurfaceTexture != null) {
                        mSurfaceTexture.updateTexImage();
                    }
                } catch (Throwable tr) {
                    Log.e(TAG, "", tr);
                }
                return;
            }
            if (mSizeChanging) {
                mSizeChanging = false;
                try {
                    if (mSurfaceTexture != null) {
                        mSurfaceTexture.updateTexImage();
                    }
                } catch (Throwable tr) {
                    Log.e(TAG, "", tr);
                }
                return;
            }
        }
        if (!mFilterChanging) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
//            if (isOESFilter()) {
//                mFilter.onDraw(mSurfaceTextureID, mGLCubeBuffer, mGLTextureBuffer);
//            } else {
            mFilter.onDraw(mGLTextureId, mGLCubeBuffer, mGLTextureBuffer);
//            }
        }
        runAll(mRunOnDrawEnd);
        try {
            if (mSurfaceTexture != null) {
                mSurfaceTexture.updateTexImage();
            }
        } catch (Throwable tr) {
            Log.e(TAG, "", tr);
        }

        if (mListener != null && mListener.needCallback()) {
            mListener.onFiltFrameDraw(getFiltFrame(gl, mOutputWidth, mOutputHeight));
        }
    }

    public void onSurfaceDestroy() {
        mFilter.destroy();
        if (mSurfaceTextureID != NO_IMAGE) {
            GLES20.glDeleteTextures(1, new int[]{
                    mSurfaceTextureID
            }, 0);
            mSurfaceTextureID = NO_IMAGE;
        }
        if (mGLTextureId != NO_IMAGE) {
            GLES20.glDeleteTextures(1, new int[]{
                    mGLTextureId
            }, 0);
            mGLTextureId = NO_IMAGE;
        }
    }

    public static Bitmap getFiltFrame(GL10 gl, int width, int height) {
        int[] iat = new int[width * height];
        IntBuffer ib = IntBuffer.allocate(width * height);
        GLES20.glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, ib);
        int[] ia = ib.array();

        // Convert upside down mirror-reversed image to right-side up normal
        // image.
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                iat[(height - i - 1) * width + j] = ia[i * width + j];
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(IntBuffer.wrap(iat));
        return bitmap;
    }

    private void runAll(Queue<Runnable> queue) {
        synchronized (queue) {
            while (!queue.isEmpty()) {
                queue.poll().run();
            }
        }
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

    public void setFilter(final GPUImageFilter filter, final List<GPUImageFilter> oldFilters) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                mFilterChanging = mIsCamera;
                mFilter = filter;
                mFilter.init();
                GLES20.glUseProgram(mFilter.getProgram());
                mFilter.onOutputSizeChanged(mOutputWidth, mOutputHeight);
            }
        });
    }

    public void deleteImage() {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                GLES20.glDeleteTextures(1, new int[]{
                        mGLTextureId
                }, 0);
                mGLTextureId = NO_IMAGE;
            }
        });
    }

    public void setImageBitmap(final Bitmap bitmap) {
        setImageBitmap(bitmap, true);
    }

    public void setImageBitmap(final Bitmap bitmap, final boolean recycle) {
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }

        runOnDraw(new Runnable() {

            @Override
            public void run() {
                if (bitmap == null || bitmap.isRecycled()) {
                    return;
                }

                try {
                    Bitmap resizedBitmap = null;
                    if (bitmap.getWidth() % 2 == 1) {
                        resizedBitmap = Bitmap.createBitmap(bitmap.getWidth() - 1, bitmap.getHeight(),
                                Bitmap.Config.ARGB_8888);
                        Canvas can = new Canvas(resizedBitmap);
                        can.drawARGB(0x00, 0x00, 0x00, 0x00);
                        can.drawBitmap(bitmap, 0, 0, null);
                        mAddedPadding = 1;
                    } else {
                        mAddedPadding = 0;
                    }

                    mGLTextureId = OpenGlUtils.loadTexture(
                            resizedBitmap != null ? resizedBitmap : bitmap, mGLTextureId, recycle);
                    if (resizedBitmap != null) {
                        resizedBitmap.recycle();
                    }
                    mImageWidth = bitmap.getWidth();
                    mImageHeight = bitmap.getHeight();
                    adjustImageScaling();
                } catch (Exception e) {

                }
            }
        });
    }


    protected int getFrameWidth() {
        return mOutputWidth;
    }

    protected int getFrameHeight() {
        return mOutputHeight;
    }

    private void adjustImageScaling() {
        float outputWidth = mOutputWidth;
        float outputHeight = mOutputHeight;
        if (mRotation == Rotation.ROTATION_270 || mRotation == Rotation.ROTATION_90) {
            outputWidth = mOutputHeight;
            outputHeight = mOutputWidth;
        }

        float ratio1;
        float ratio2;
        float ratioMax;
        int imageWidthNew;
        int imageHeightNew;
        float ratioWidth;
        float ratioHeight;

        if (mPreviewWidth != 0 && mImageWidth == 0) {
            ratio1 = outputWidth / mPreviewWidth;
            ratio2 = outputHeight / mPreviewHeight;
            ratioMax = Math.max(ratio1, ratio2);
            imageWidthNew = Math.round(mPreviewWidth * ratioMax);
            imageHeightNew = Math.round(mPreviewHeight * ratioMax);
            ratioWidth = outputWidth / imageWidthNew;
            ratioHeight = outputHeight / imageHeightNew;
        } else {
            ratio1 = outputWidth / mImageWidth;
            ratio2 = outputHeight / mImageHeight;
            ratioMax = Math.max(ratio1, ratio2);
            imageWidthNew = Math.round(mImageWidth * ratioMax);
            imageHeightNew = Math.round(mImageHeight * ratioMax);
            ratioWidth = imageWidthNew / outputWidth;
            ratioHeight = imageHeightNew / outputHeight;
        }

        float[] cube = CUBE;
        float[] textureCords = TextureRotationUtil.getRotation(mRotation, mFlipHorizontal, mFlipVertical);
        if (mScaleType == ImageView.ScaleType.CENTER_CROP) {
            float distHorizontal = 0;
            float distVertical = 0;

            textureCords = new float[]{
                    addDistance(textureCords[0], distHorizontal), addDistance(textureCords[1], distVertical),
                    addDistance(textureCords[2], distHorizontal), addDistance(textureCords[3], distVertical),
                    addDistance(textureCords[4], distHorizontal), addDistance(textureCords[5], distVertical),
                    addDistance(textureCords[6], distHorizontal), addDistance(textureCords[7], distVertical),
            };
        } else if(mScaleType == ImageView.ScaleType.FIT_CENTER) {
            RectF showRectF = new RectF();
            float rotationTW = mImageWidth;
            float rotationTH = mImageHeight;
            if (mRotation == Rotation.ROTATION_270 || mRotation == Rotation.ROTATION_90) {
                rotationTW = mImageHeight;
                rotationTH = mImageWidth;
            }
            float textureRatio = 1.0f * rotationTW / rotationTH;
            float displayRatio = 1.0f * mOutputWidth / mOutputHeight;
            float newWidth;
            float newHeight;
            if (textureRatio >= displayRatio){//texture按照比例缩放后宽会和display一样大
                newWidth = mOutputWidth;
                newHeight = 1.0f * mOutputWidth * rotationTH / rotationTW;
            } else{
                newHeight = mOutputHeight;
                newWidth = 1.0f * mOutputHeight * rotationTW / rotationTH;
            }
            float dx = (mOutputWidth - newWidth) / 2;
            float dy = (mOutputHeight - newHeight) / 2;
            showRectF.set(dx, dy, newWidth + dx, newHeight + dy);
            cube = Utils.getStandardVertex(showRectF, mOutputWidth, mOutputHeight, Utils.VERTEX_MODE.ABDC);
        } else {
            cube = new float[]{
                    CUBE[0] / ratioHeight, CUBE[1] / ratioWidth,
                    CUBE[2] / ratioHeight, CUBE[3] / ratioWidth,
                    CUBE[4] / ratioHeight, CUBE[5] / ratioWidth,
                    CUBE[6] / ratioHeight, CUBE[7] / ratioWidth,
            };
        }

        mGLCubeBuffer.clear();
        mGLCubeBuffer.put(cube).position(0);
        mGLTextureBuffer.clear();
        mGLTextureBuffer.put(textureCords).position(0);
    }

    private float addDistance(float coordinate, float distance) {
        return coordinate == 0.0f ? distance : 1 - distance;
    }

    public void setRotationCamera(final Rotation rotation, final boolean flipHorizontal,
                                  final boolean flipVertical) {
        synchronized (mChangeingLock) {
            mRotationChanging = true;
            setRotation(rotation, flipVertical, flipHorizontal);
        }
    }

    public void setRotation(final Rotation rotation) {
        mRotation = rotation;
        adjustImageScaling();
    }

    public void setRotation(final Rotation rotation,
                            final boolean flipHorizontal, final boolean flipVertical) {
        mFlipHorizontal = flipHorizontal;
        mFlipVertical = flipVertical;
        setRotation(rotation);
    }

    public Rotation getRotation() {
        return mRotation;
    }

    public boolean isFlippedHorizontally() {
        return mFlipHorizontal;
    }

    public boolean isFlippedVertically() {
        return mFlipVertical;
    }

    protected void runOnDraw(final Runnable runnable) {
        synchronized (mRunOnDraw) {
            mRunOnDraw.add(runnable);
        }
    }

    protected void runOnDrawEnd(final Runnable runnable) {
        synchronized (mRunOnDrawEnd) {
            mRunOnDrawEnd.add(runnable);
        }
    }

    public void setFiltFrameListener(FiltFrameListener listener) {
        this.mListener = listener;
    }

    private int createTextureID()
    {
        int[] texture = new int[1];

        GLES20.glGenTextures(1, texture, 0);
        if (!isNotSupportOES() && !isNotSupportVideoRender()) {
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        }

        return texture[0];
    }

//    public boolean isOESFilter() {
//        if (mFilter == null) {
//            return false;
//        }
//
//        if (mFilter.getClass() == GPUImageOESFilter.class
//                || mFilter.getClass() == GPUImageHDROESFilter.class) {
//            return true;
//        }
//
//        if (mFilter instanceof GPUImageFilterGroup) {
//            if(((GPUImageFilterGroup)mFilter).getMergedFilters() != null && ((GPUImageFilterGroup)mFilter).getMergedFilters().size() > 0) {
//                Class classOes = ((GPUImageFilterGroup) mFilter).getMergedFilters().get(0).getClass();
//                return classOes == GPUImageOESFilter.class || classOes == GPUImageHDROESFilter.class;
//            } else {
//                return false;
//            }
//        }
//
//        return false;
//    }


    public boolean isRecording() {
        return false;
    }


    /**
     * 是否OES支持异常机型
     *
     * @return
     */
    public static boolean isNotSupportOES() {
        return
                "Galaxy Nexus".equals(Build.MODEL)
                        || (isSony() && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2)
                        || "GT-I8552".equals(Build.MODEL)
                        || "SM-T110".equals(Build.MODEL)
                        || "SM-T211".equals(Build.MODEL)
//						|| "GT-I9502".equals(Build.MODEL)
                        || "XT910".equals(Build.MODEL)
                        || "SM-G3818".equals(Build.MODEL);
    }


    /**
     * 是否是索尼机型
     *
     * @return
     */
    public static boolean isSony() {
        return Build.BRAND.equalsIgnoreCase("SEMC") || Build.BRAND.equalsIgnoreCase("Sony");
    }


    /**
     * 是否是录像不支持预览渲染机型
     *
     * @return
     */
    public static boolean isNotSupportVideoRender() {
//		Loger.d("Test", Build.MODEL +  " " + Build.BRAND);
        if ("GT-I9300".equals(Build.MODEL)
                || "HUAWEI C8950D".equals(Build.MODEL)
                || Build.MODEL.contains("HTC Sensation XE")
                || "C2305".equals(Build.MODEL)
                || "GT-S7270".equals(Build.MODEL)
                || "GT-S7275".equals(Build.MODEL)
                || "GT-S7272".equals(Build.MODEL)
                || "SM-T210".equals(Build.MODEL)
                || "ST26a".equals(Build.MODEL)
                || Build.MODEL.toLowerCase().contains("htc desire 310")
                || ("Coolpad".equals(Build.BRAND) && "9900".equals(Build.MODEL))
                || "XT910".equals(Build.MODEL)
                || Build.MODEL.toLowerCase().contains("vs840")
                || "SCH-I435".equals(Build.MODEL)
                || ("SM-N910F".equals(Build.MODEL) && !isLollipop())) {
            return true;
        }
        return false;
    }

    /**
     * 系统版本是否高于5.0
     *
     * @return
     */
    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= 21;
    }


}
