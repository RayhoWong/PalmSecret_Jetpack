#include <jni.h>
#include <android/log.h>

#include <vector>
#include <math.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include "opencv2/core/types.hpp"
#include "MovingLeastSquare.h"

#ifndef _Included_com_picstudio_photoeditorplus_stasm_MlsUtils
#define _Included_com_picstudio_photoeditorplus_stasm_MlsUtils

using namespace cv;
using namespace std;

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jfloatArray JNICALL Java_com_picstudio_photoeditorplus_stasm_MlsUtils_mlsWithRigid
  (JNIEnv * env, jclass clazz, jint cacheKey, jfloatArray srcGridPoints, jfloatArray srcControlPoints, jfloatArray targetControlPoints, jboolean isRigid);
JNIEXPORT void JNICALL Java_com_picstudio_photoeditorplus_stasm_MlsUtils_clearMlsTempCache(JNIEnv *env,
                                                                                                 jclass type);

float clamp(float value, float minValue, float maxValue);

#ifdef __cplusplus
}
#endif
#endif
