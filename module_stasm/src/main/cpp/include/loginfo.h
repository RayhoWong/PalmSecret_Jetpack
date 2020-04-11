//
// Created by denghaohua on 2019/11/28.
//

#ifndef GA0FACEDEMO_LOGINFO_H
#define GA0FACEDEMO_LOGINFO_H

#include <jni.h>
#include<android/log.h>
#define TAG "face" // 这个是自定义的LOG的标识
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG ,__VA_ARGS__) // 定义LOGW类型

class loginfo {
public:
    void testClass();
};


#endif //GA0FACEDEMO_LOGINFO_H
