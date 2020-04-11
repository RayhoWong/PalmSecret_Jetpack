#include <jni.h>
#include <string>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include <android/log.h>
#include <GLES2/gl2.h>

using namespace std;
using namespace cv;


#define LOG_TAG "Stasm-FaceDetect"
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR,   LOG_TAG, __VA_ARGS__))

std::string getNativeString(JNIEnv *env, jstring _str)
{
    const char *str = env->GetStringUTFChars(_str, nullptr);
    std::string result(str);
    env->ReleaseStringUTFChars(_str, str);

    return result;
}

extern "C"
JNIEXPORT jfloatArray JNICALL
Java_com_picstudio_photoeditorplus_stasm_StasmFaceDetectionSdk_nativeGetTriangleList(JNIEnv *env,
                                                                                        jclass type,
                                                                                        jint width_,
                                                                                        jint height_,
                                                                                        jfloatArray points_,
                                                                                        jint length_,
                                                                                        jint offset) {
    jfloat *points = env->GetFloatArrayElements(points_, NULL);
    Subdiv2D subdiv(Rect(0, 0, width_, height_));
    for (int i = offset; i < length_; i+=2) {
        subdiv.insert(Point2f(points[i], points[i + 1]));
    }
    vector<Vec6f> triangleList;
    subdiv.getTriangleList(triangleList);
    LOGE("triangleList size = %d", triangleList.size());
    env->ReleaseFloatArrayElements(points_, points, 0);

    int fullDataSize = triangleList.size() * 6;
    float *pointsData = new float[fullDataSize];
    for (int i = 0; i < triangleList.size(); i++) {
        Vec6f triangle = triangleList[i];
        for (int j = 0; j < 6; j++) {
            pointsData[i * 6 + j] = triangle[j];
        }
    }
    jfloatArray result = env->NewFloatArray(fullDataSize);
    env->SetFloatArrayRegion(result, 0, fullDataSize, pointsData);
    delete[] pointsData;
    return result;
}

extern "C"
JNIEXPORT jfloatArray JNICALL
Java_com_picstudio_photoeditorplus_stasm_StasmFaceDetectionSdk_nativeGetMinAreaRect(
        JNIEnv *env, jclass type, jfloatArray points_, jintArray indexes_) {
    jfloat *points = env->GetFloatArrayElements(points_, NULL);
    jint *indexes = env->GetIntArrayElements(indexes_, NULL);
    int indexesSize = env->GetArrayLength(indexes_);

    vector<Point2f> pointList;
    int index;
    for (int i = 0; i < indexesSize; i++) {
        index = indexes[i];
        pointList.push_back(Point2f(points[4 + index * 2], points[5 + index * 2]));
    }
    RotatedRect rotatedRect = minAreaRect(pointList);
    env->ReleaseFloatArrayElements(points_, points, 0);
    env->ReleaseIntArrayElements(indexes_, indexes, 0);

    jfloatArray result = env->NewFloatArray(5);
    float* data = new float[5];
    data[0] = rotatedRect.center.x;
    data[1] = rotatedRect.center.y;
    data[2] = rotatedRect.angle;
    data[3] = rotatedRect.size.width;
    data[4] = rotatedRect.size.height;
    env->SetFloatArrayRegion(result, 0, 5, data);
    delete[] data;
    return result;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_picstudio_photoeditorplus_stasm_StasmFaceDetectionSdk_loadTexture(JNIEnv *env,
                                                                               jclass type,
                                                                               jlong imageDataAddr,
                                                                               jint width,
                                                                               jint height,
                                                                               jint textureId) {
    if (textureId != -1) {
        glBindTexture(GL_TEXTURE_2D, static_cast<GLuint>(textureId));
        glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, (int)width, (int)height, GL_RGBA, GL_UNSIGNED_BYTE, (uchar*)imageDataAddr);
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}