//
// Created by denghaohua on 2019/12/3.
//
#include <jni.h>
#include <malloc.h>
#include <cstring>
#include <string>
#include "include/checkApp.h"

const char * packageName = "com.palmsecret.horoscope";
char * getPackageName(JNIEnv *env, jobject context) {
//    //上下文对象
    jclass contextClass  = (jclass) env->NewGlobalRef(env->FindClass("android/content/Context"));
    jmethodID getPackageNameId = env->GetMethodID(contextClass,"getPackageName","()Ljava/lang/String;");
    jstring packNameString =  (jstring)env->CallObjectMethod(context, getPackageNameId);
    return Jstring2CStr(env,packNameString);
}

int checkValidity(char * pack) {
    std::string a = pack;
    std::string b = packageName ;
    return strcmp(pack,packageName);
}

char * Jstring2CStr(JNIEnv *env, jstring jstr) {
    char * rtn = NULL;
    jclass clsstring = env->FindClass("java/lang/String");
    jstring strencode = env->NewStringUTF("GB2312");
    jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
    jsize alen = env->GetArrayLength(barr);
    jbyte *ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0) {
        rtn = (char *) malloc(alen + 1);
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    return rtn;
}

