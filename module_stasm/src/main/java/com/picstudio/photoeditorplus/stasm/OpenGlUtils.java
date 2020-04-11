/*
 * Copyright (C) 2012 CyberAgent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.picstudio.photoeditorplus.stasm;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.hardware.Camera.Size;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class OpenGlUtils {
    public static final int NO_TEXTURE = -1;

    public static int loadTexture(final String textureFilePath, final int usedTexId) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(textureFilePath);
        } catch (Throwable tr ) {
            tr.printStackTrace();
        }
        if (bitmap != null) {
            return loadTexture(bitmap, usedTexId);
        }
        return NO_TEXTURE;
    }

    public static int loadTexture(final Bitmap img, final int usedTexId) {
        return loadTexture(img, usedTexId, true);
    }

    public static int loadTexture(final Bitmap img, final int usedTexId, final boolean recycle) {
        if(img == null || img.isRecycled()){
            return NO_TEXTURE;
        } else {
            int textures[] = new int[1];
            if (usedTexId == NO_TEXTURE) {
                GLES20.glGenTextures(1, textures, 0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, img, 0);
            } else {
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, usedTexId);
                GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, img);
                textures[0] = usedTexId;
            }
            if (recycle) {
                img.recycle();
            }
            return textures[0];
        }
    }

    public static int loadTexture(Resources res, final int drawableId, final int usedTexId, final boolean recycle) {
        Bitmap bitmap = BitmapFactory.decodeResource(res, drawableId);
        return loadTexture(bitmap, usedTexId, recycle);
    }

    public static int loadTexture(final IntBuffer data, final Size size, final int usedTexId) {
        int textures[] = new int[1];
        if (usedTexId == NO_TEXTURE) {
            GLES20.glGenTextures(1, textures, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, size.width, size.height,
                    0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, data);
        } else {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, usedTexId);
//            if (sizeChanged) {
//                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//                        GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
//                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//                        GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//                        GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
//                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//                        GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, size.width, size.height,
                        0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, data);
//            } else {
//                GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, size.width,
//                        size.height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, data);
//            }
            textures[0] = usedTexId;
        }
        return textures[0];
    }

    public static int loadTexture(final ByteBuffer data, int width, int height, final int usedTexId) {
        int textures[] = new int[1];
        if (usedTexId == NO_TEXTURE) {
            GLES20.glGenTextures(1, textures, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height,
                    0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, data);
        } else {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, usedTexId);
            GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, width,
                    height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, data);
            textures[0] = usedTexId;
        }
        return textures[0];
    }

    public static int loadTextureAsBitmap(final IntBuffer data, final Size size, final int usedTexId) {
        Bitmap bitmap = Bitmap
                .createBitmap(data.array(), size.width, size.height, Config.ARGB_8888);
        return loadTexture(bitmap, usedTexId);
    }

    public static int loadShader(final String strSource, final int iType) {
        int[] compiled = new int[1];
        int iShader = GLES20.glCreateShader(iType);
        GLES20.glShaderSource(iShader, strSource);
        GLES20.glCompileShader(iShader);
        GLES20.glGetShaderiv(iShader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.d("Load Shader Failed", "Compilation\n" + GLES20.glGetShaderInfoLog(iShader));
            return 0;
        }
        return iShader;
    }

    public static int loadProgram(final String strVSource, final String strFSource) {
        int iVShader;
        int iFShader;
        int iProgId;
        int[] link = new int[1];
        iVShader = loadShader(strVSource, GLES20.GL_VERTEX_SHADER);
        if (iVShader == 0) {
            Log.d("Load Program", "Vertex Shader Failed");
            return 0;
        }
        iFShader = loadShader(strFSource, GLES20.GL_FRAGMENT_SHADER);
        if (iFShader == 0) {
            Log.d("Load Program", "Fragment Shader Failed");
            return 0;
        }

        iProgId = GLES20.glCreateProgram();

        GLES20.glAttachShader(iProgId, iVShader);
        GLES20.glAttachShader(iProgId, iFShader);

        GLES20.glLinkProgram(iProgId);

        GLES20.glGetProgramiv(iProgId, GLES20.GL_LINK_STATUS, link, 0);
        if (link[0] <= 0) {
            Log.d("Load Program", "Linking Failed");
            return 0;
        }
        GLES20.glDeleteShader(iVShader);
        GLES20.glDeleteShader(iFShader);
        return iProgId;
    }

    public static float rnd(final float min, final float max) {
        float fRandNum = (float) Math.random();
        return min + (max - min) * fRandNum;
    }

    public static int[] initTextureID(int width, int height) {
        int[] mTextureOutID = new int[]{-1};
        GLES20.glGenTextures(1, mTextureOutID, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureOutID[0]);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE, null);
        return mTextureOutID;
    }

    public static void bindFrameBufferToTexture(int width, int height, int[] framebuffers, int[] textures) {
        if (textures[0] != -1) {
            GLES20.glDeleteTextures(1, textures, 0);
            textures[0] = -1;
        }
        if (framebuffers[0] != -1) {
            GLES20.glDeleteFramebuffers(1, framebuffers, 0);
            framebuffers[0] = -1;
        }
        GLES20.glGenFramebuffers(1, framebuffers, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffers[0]);
        GLES20.glGenTextures(1, textures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textures[0], 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public static FloatBuffer createFloatBuffer(int size) {
        ByteBuffer localByteBuffer = ByteBuffer.allocateDirect(size * 4);
        localByteBuffer.order(ByteOrder.nativeOrder());
        return localByteBuffer.asFloatBuffer();
    }

    public static FloatBuffer createFloatBuffer(float[] src) {
        FloatBuffer result = createFloatBuffer(src.length);
        result.put(src);
        result.position(0);
        return result;
    }

    public static ShortBuffer createShortBuffer(short[] src) {
        ShortBuffer result = ByteBuffer.allocateDirect(src.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
        result.put(src).position(0);
        return result;
    }

    public static void checkGLError(String var0) {
        int var1 = GLES20.glGetError();
        if(var1 != 0) {
            String var2 = var0 + ": glError 0x" + Integer.toHexString(var1);
            Log.e("GL utils", var2);
        }

    }

    public static boolean supportEs3(Activity activity) {
        final ActivityManager activityManager =
                (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo =
                activityManager.getDeviceConfigurationInfo();
        return configurationInfo.reqGlEsVersion >= 0x30000;
    }

    public static void initFBO(int width, int height, int[] frameBuffers, int[] frameBufferTextures) {
        GLES20.glGenFramebuffers(frameBuffers.length, frameBuffers, 0);
        GLES20.glGenTextures(frameBufferTextures.length, frameBufferTextures, 0);

        for (int i = 0; i < frameBuffers.length; i++) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, frameBufferTextures[i]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                    GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffers[i]);
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                    GLES20.GL_TEXTURE_2D, frameBufferTextures[i], 0);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        }
    }

    public static float[] screenCoorToGLCoor(RectF rect, int width, int height) {
        float[] result = new float[8];
        System.arraycopy(screenCoorToGLCoor(new float[]{rect.left, rect.top}, width, height, 0, false), 0, result, 0, 2);
        System.arraycopy(screenCoorToGLCoor(new float[]{rect.right, rect.top}, width, height, 0, false), 0, result, 2, 2);
        System.arraycopy(screenCoorToGLCoor(new float[]{rect.left, rect.bottom}, width, height, 0, false), 0, result, 4, 2);
        System.arraycopy(screenCoorToGLCoor(new float[]{rect.right, rect.bottom}, width, height, 0, false), 0, result, 6, 2);
        return result;
    }

    public static float[] screenCoorToGLCoor(float[] point, int width, int height, int orientation, boolean isBackCamera) {
        float x = 2.0F * point[0] / width - 1.0f; // (point[0] / width) * 2 - 1;
        float y = 2.0F * point[1] / height - 1.0F ; // 1 - (point[1] / height) * 2;

        if (isBackCamera) {
            x = -x;
        }

        float[] pointf;
        if (orientation == 1) {
            pointf = new float[]{-y, x};
        } else if (orientation == 2) {
            pointf = new float[]{y, -x};
        } else if (orientation == 3) {
            pointf = new float[]{-x, -y};
        } else {
            pointf = new float[] { x, y};
        }
        return pointf;
    }
}
