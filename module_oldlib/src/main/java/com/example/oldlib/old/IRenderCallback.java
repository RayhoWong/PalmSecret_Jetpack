package com.example.oldlib.old;

import android.graphics.SurfaceTexture;

public interface IRenderCallback {
    void onSurfaceTextureCreated(SurfaceTexture surfaceTexture);
    void onFrameAvaliable(long frameTimeNanos);
}