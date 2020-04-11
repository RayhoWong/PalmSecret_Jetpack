//
// Created by denghaohua on 2019/12/3.
//

#ifndef GA0FACEDEMO_CHECKAPP_H
#define GA0FACEDEMO_CHECKAPP_H
#include <jni.h>

char * getPackageName(JNIEnv *env, jobject context);
int checkValidity( char *);
char * Jstring2CStr(JNIEnv *env, jstring jstr);

#endif //GA0FACEDEMO_CHECKAPP_H
