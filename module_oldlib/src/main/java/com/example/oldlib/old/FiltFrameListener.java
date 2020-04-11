package com.example.oldlib.old;

import android.graphics.Bitmap;

public interface FiltFrameListener {
    boolean needCallback();
    void onFiltFrameDraw(Bitmap bitmap);
}
