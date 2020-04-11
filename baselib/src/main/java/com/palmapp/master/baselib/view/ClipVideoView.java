package com.palmapp.master.baselib.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import com.palmapp.master.baselib.R;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

public class ClipVideoView extends GLSurfaceView {
    /**
     * MediaPlayer
     */
    private MediaPlayer mediaPlayer;
    /**
     * OnPreparedListener
     */
    private MediaPlayer.OnPreparedListener mOnPreparedListener;

    private OnInitCompleteListener onInitCompleteListener;

    /**
     * 构造函数
     */
    public ClipVideoView(Context context) {
        this(context, null);
    }

    /**
     * 构造函数
     */
    public ClipVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setOnInitCompleteListener(OnInitCompleteListener onInitCompleteListener) {
        this.onInitCompleteListener = onInitCompleteListener;
    }

    /**
     * 初始化
     */
    private void init() {
        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(true);
        setRenderer(new VideoRenderer(getContext()));
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    /**
     * 设置视频播放监听器
     */
    public void setPreparedListener(MediaPlayer.OnPreparedListener onPreparedListener) {
        mOnPreparedListener = onPreparedListener;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    /**
     * 销毁
     */
    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private class VideoRenderer implements Renderer {
        /**
         * TAG
         */
        private static final String TAG = "VideoRenderer";
        /**
         * context
         */
        private Context context;

        /**
         * 纹理id
         */
        int textureId = -1;
        /**
         * 坐标点
         */
        private final int COORDS_PER_VERTEX = 3;
        /**
         * 坐标点
         */
        private final int TEXCOORDS_PER_VERTEX = 2;
        /**
         * float size
         */
        private static final int FLOAT_SIZE = 4;
        /**
         * 定点坐标
         */
        private final float[] QUAD_COORDS = {
                -1.0f, -1.0f, 0.0f,  // bottom left
                1.0f, -1.0f, 0.0f,  // bottom right
                -1.0f, 1.0f, 0.0f,  // top left
                1.0f, 1.0f, 0.0f  // top right
        };
        /**
         * 纹理坐标
         */
        private float[] quadTexCoords = {
                0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f
        };
        /**
         * index
         */
        private final short[] index = {0, 1, 2, 1, 2, 3};
        /**
         * 顶点
         */
        private FloatBuffer quadVertices;
        /**
         * 纹理
         */
        private FloatBuffer quadTexCoord;
        /**
         * 索引
         */
        private ShortBuffer shortBuffer;
        /**
         * program
         */
        private int quadProgram = -1;
        /**
         * 顶点参数索引
         */
        private int quadPositionParam = -1;
        /**
         * 纹理参数索引
         */
        private int quadTexCoordParam = -1;
        /**
         * oes
         */
        private int uTextureSamplerLocation = -1;
        /**
         * 是否有新的一针视频
         */
        private boolean updateSurface = false;
        /**
         * SurfaceTexture
         */
        private SurfaceTexture surfaceTexture;
        /**
         * 锁
         */
        private Object lock = new Object();

        /**
         * 构造函数
         */
        public VideoRenderer(Context context) {
            this.context = context;
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            int[] textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);
            textureId = textures[0];
            int textureTarget = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
            GLES20.glBindTexture(textureTarget, textureId);
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);


            int vertexShader =
                    ShaderUtil.loadGLShader(TAG, context, GLES20.GL_VERTEX_SHADER, R.raw.base_vertex);
            int fragmentShader =
                    ShaderUtil.loadGLShader(
                            TAG, context, GLES20.GL_FRAGMENT_SHADER, R.raw.base_fragment);

            quadProgram = GLES20.glCreateProgram();
            GLES20.glAttachShader(quadProgram, vertexShader);
            GLES20.glAttachShader(quadProgram, fragmentShader);
            GLES20.glLinkProgram(quadProgram);
            GLES20.glUseProgram(quadProgram);

            ShaderUtil.checkGLError(TAG, "Program creation");

            quadPositionParam = GLES20.glGetAttribLocation(quadProgram, "a_Position");
            quadTexCoordParam = GLES20.glGetAttribLocation(quadProgram, "a_TexCoordinate");
            uTextureSamplerLocation = GLES20.glGetUniformLocation(quadProgram, "u_Texture");
            ShaderUtil.checkGLError(TAG, "Program parameters");

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            surfaceTexture = new SurfaceTexture(textureId);
            Surface surface = new Surface(surfaceTexture);
            surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                @Override
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    synchronized (lock) {
                        updateSurface = true;
                    }
                }
            });
            mediaPlayer.setSurface(surface);
            if (onInitCompleteListener != null)
                onInitCompleteListener.onComplete(mediaPlayer);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            quadVertices = ByteBuffer.allocateDirect(QUAD_COORDS.length * FLOAT_SIZE)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            quadVertices.put(QUAD_COORDS);
            quadVertices.position(0);

            float videoRatio = 1080f / 1920f;
            Log.d(TAG, "width:" + width + "  height:" + height);
            float viewRatio = ((float) width) / height;
            Log.d(TAG, "videoRatio:" + videoRatio + "  viewRatio:" + viewRatio);
            if (viewRatio < videoRatio) {
                float s = (1 - (1920f / 1080f * viewRatio)) / 2.0F;
                float[] texCoord = {

                        0.0f + s, 1.0f,
                        1.0f - s, 1.0f,
                        0.0f + s, 0.0f,
                        1.0f - s, 0.0f
                };
                Log.d(TAG, Arrays.toString(texCoord));
                quadTexCoord = ByteBuffer.allocateDirect(texCoord.length * FLOAT_SIZE)
                        .order(ByteOrder.nativeOrder())
                        .asFloatBuffer();
                quadTexCoord.put(texCoord);
                quadTexCoord.position(0);
            } else if (viewRatio > videoRatio) {
                float s = (1 - (1080f / (1920f * viewRatio))) / 2.0F;
                float[] texCoord = {
                        0.0f, 1.0f - s,
                        1.0f, 1.0f - s,
                        0.0f, 0.0f + s,
                        1.0f, 0.0f + s
                };
                Log.d(TAG, Arrays.toString(texCoord));
                quadTexCoord = ByteBuffer.allocateDirect(texCoord.length * FLOAT_SIZE)
                        .order(ByteOrder.nativeOrder())
                        .asFloatBuffer();
                quadTexCoord.put(texCoord);
                quadTexCoord.position(0);
            } else {
                quadTexCoord = ByteBuffer.allocateDirect(quadTexCoords.length * FLOAT_SIZE)
                        .order(ByteOrder.nativeOrder())
                        .asFloatBuffer();
                quadTexCoord.put(quadTexCoords);
                quadTexCoord.position(0);
            }


            shortBuffer = ByteBuffer.allocateDirect(index.length * 2)
                    .order(ByteOrder.nativeOrder())
                    .asShortBuffer();
            shortBuffer.put(index);
            shortBuffer.position(0);


        }

        @Override
        public void onDrawFrame(GL10 gl) {
            synchronized (lock) {
                if (updateSurface) {
                    surfaceTexture.updateTexImage();
                    updateSurface = false;
                }
            }
            GLES20.glUseProgram(quadProgram);
            // Set the vertex positions.
            quadVertices.position(0);
            GLES20.glEnableVertexAttribArray(quadPositionParam);
            GLES20.glVertexAttribPointer(
                    quadPositionParam, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, quadVertices);
            // Set the texture coordinates.
            quadTexCoord.position(0);
            GLES20.glEnableVertexAttribArray(quadTexCoordParam);
            GLES20.glVertexAttribPointer(
                    quadTexCoordParam, TEXCOORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, quadTexCoord);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
            GLES20.glUniform1i(uTextureSamplerLocation, 0);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, index.length, GLES20.GL_UNSIGNED_SHORT, shortBuffer);
            // Disable vertex arrays
            GLES20.glDisableVertexAttribArray(quadPositionParam);
            GLES20.glDisableVertexAttribArray(quadTexCoordParam);
        }
    }

    public interface OnInitCompleteListener {
        void onComplete(MediaPlayer mp);
    }
}
