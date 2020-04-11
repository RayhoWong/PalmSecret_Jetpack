package com.palmapp.master;

import android.content.res.AssetManager;

/**
 * @author :     xiemingrui
 * @since :      2020/3/4
 */
public class InterpreterJNI {
    static {
        System.loadLibrary("des");
        System.loadLibrary("desJni");
    }

    public static native byte[] decryptModel(String fileName, String pagname, AssetManager assetManager);
}
