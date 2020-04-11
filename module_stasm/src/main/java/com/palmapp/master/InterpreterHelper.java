package com.palmapp.master;

import android.app.Activity;
import android.content.Context;
import android.util.Log;


import org.tensorflow.lite.Interpreter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author :     xiemingrui
 * @since :      2020/3/4
 */
public class InterpreterHelper {

    public static Interpreter decryptModel(Context activity, String fileName) {
        byte[] bytes = InterpreterJNI.decryptModel(fileName, "com.palmsecret.horoscope", activity.getAssets());
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bytes.length);
        byteBuffer.order(ByteOrder.nativeOrder());
        byteBuffer.put(bytes);
        Interpreter.Options options =  new Interpreter.Options();
        options.setNumThreads(2);
        Interpreter mInterpreter = new Interpreter(byteBuffer,options);
        Log.e("length", bytes.length + " ");
        return mInterpreter;
    }

}
