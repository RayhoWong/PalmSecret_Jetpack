#include <jni.h>
#include <string>
#include <Treble_DES.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include <checkApp.h>


extern "C" JNIEXPORT jstring JNICALL
Java_com_jiubang_desdemo_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_palmapp_master_InterpreterJNI_decryptModel(JNIEnv *env, jclass clazz,
                                                    jstring file_name, jstring pagName, jobject asset_manager) {
    // 你的包名，可以通过checkapp.cpp获取，也可以手动传入。
    Treble_DES trebleDes = Treble_DES(Jstring2CStr(env,pagName));
    AAssetManager* mgr = AAssetManager_fromJava(env, asset_manager);
    if(mgr==nullptr){
        LOGW(" %s","AAssetManager==NULL");
        return nullptr;
    }
    jboolean iscopy;
    const char * mfile = (char *)env->GetStringUTFChars(file_name,&iscopy);
    AAsset* asset = AAssetManager_open(mgr, mfile,AASSET_MODE_UNKNOWN);
    if(asset==nullptr){
        LOGW(" %s","asset==NULL");
        return nullptr;
    }
    off_t bufferSize = AAsset_getLength(asset);
    char * buffer = new char[bufferSize];
    AAsset_read(asset, buffer, bufferSize);
    char * outputBuffer = new char[bufferSize];
    // 由于现在加密是部分加密，所以取消了这个变量，但是还是写上了，以防万一
    int fillLength = 0;
    trebleDes.decrypt(buffer,outputBuffer,bufferSize,fillLength);
    jbyteArray result = env->NewByteArray(bufferSize - fillLength);
    env ->SetByteArrayRegion(result, 0,bufferSize - fillLength, (jbyte *)(outputBuffer));
    AAsset_close(asset);
    env->DeleteLocalRef(asset_manager);
    return result;}