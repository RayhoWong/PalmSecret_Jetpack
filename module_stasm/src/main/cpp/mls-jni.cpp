#include "mls-jni.h"
#include <algorithm>
#include <hash_map>

#define LOG_TAG "Mls"


JNIEXPORT void JNICALL Java_com_picstudio_photoeditorplus_stasm_MlsUtils_clearMlsTempCache(JNIEnv *env,
                                                                                                 jclass type)
{

}

JNIEXPORT jfloatArray JNICALL Java_com_picstudio_photoeditorplus_stasm_MlsUtils_mlsWithRigid
        (JNIEnv * env, jclass clazz, jint cacheKey, jfloatArray srcGridPoints, jfloatArray srcControlPoints, jfloatArray targetControlPoints, jboolean isRigid)
{
    // 赋值v（原始网格）, p（控制点）, q（目标控制点）
    jfloat* body = env->GetFloatArrayElements(srcGridPoints, 0);
    int size = (int)env->GetArrayLength(srcGridPoints) / 2;
    Mat v = Mat(2, size, CV_32F);
    for (int i = 0; i < size; i++)
    {
        v.at<float>(0, i) = body[i * 2];
        v.at<float>(1, i) = body[i * 2 + 1];
    }
    env->ReleaseFloatArrayElements(srcGridPoints, body, 0);

    body = env->GetFloatArrayElements(srcControlPoints, 0);
    size = (int)env->GetArrayLength(srcControlPoints) / 2;
    Mat p = Mat(2, size, CV_32F);
    for (int i = 0; i < size; i++)
    {
        p.at<float>(0, i) = body[i * 2];
        p.at<float>(1, i) = body[i * 2 + 1];
    }
    env->ReleaseFloatArrayElements(srcControlPoints, body, 0);

    body = env->GetFloatArrayElements(targetControlPoints, 0);
    size = (int)env->GetArrayLength(targetControlPoints) / 2;
    Mat q = Mat(2, size, CV_32F);
    for (int i = 0; i < size; i++)
    {
        q.at<float>(0, i) = body[i * 2];
        q.at<float>(1, i) = body[i * 2 + 1];
    }
    env->ReleaseFloatArrayElements(targetControlPoints, body, 0);

    Mat w = precomputeWeights(p, v, 1.0);

    Mat fv;
    if (isRigid)
    {
        typeRigid mlsd = precomputeRigid(p, v, w);
        fv = PointsTransformRigid(w, mlsd, q);
    } else
    {
        vector<_typeA> tA = precomputeSimilar(p, v, w);
        fv = PointsTransformSimilar(w, tA, q);
    }

    // 返回结果，长度等于原始网格点
    int length = fv.cols * 2;
    float* resultArray = new float[length];
    for (int i = 0; i < fv.cols; i++)
    {
        resultArray[i * 2] = fv.at<float>(0, i);
        resultArray[i * 2 + 1] = fv.at<float>(1, i);
    }
    jfloatArray result;
    result = env->NewFloatArray(length);
    env->SetFloatArrayRegion(result, 0, length, resultArray);

    delete [] resultArray;
    return result;
}

float clamp(float value, float minValue, float maxValue)
{
    float temp = minValue;
    if (value > minValue) {
        temp = value;
    }
    if (temp > maxValue) {
        return maxValue;
    }
    return temp;
}