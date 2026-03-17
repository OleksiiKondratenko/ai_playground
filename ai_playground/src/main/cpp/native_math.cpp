#include <jni.h>
#include <string>
#include <nlohmann/json.hpp>

extern "C" {

JNIEXPORT jint JNICALL
Java_com_aiplayground_mathlib_NativeMath_nativeAdd(JNIEnv *, jobject, jint a, jint b) {
    return a + b;
}

JNIEXPORT jint JNICALL
Java_com_aiplayground_mathlib_NativeMath_nativeMultiply(JNIEnv *, jobject, jint a, jint b) {
    return a * b;
}

JNIEXPORT jstring JNICALL
Java_com_aiplayground_mathlib_NativeMath_nativeToJson(JNIEnv *env, jobject, jint a, jint b, jint sum) {
    nlohmann::json j;
    j["a"] = a;
    j["b"] = b;
    j["sum"] = sum;
    std::string result = j.dump();
    return env->NewStringUTF(result.c_str());
}

}
