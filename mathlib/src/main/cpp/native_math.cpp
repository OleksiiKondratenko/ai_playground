#include <jni.h>
#include <string>
#include <boost/version.hpp>
#include <boost/lexical_cast.hpp>

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
Java_com_aiplayground_mathlib_NativeMath_nativeBoostVersion(JNIEnv *env, jobject) {
    std::string version = std::to_string(BOOST_VERSION / 100000) + "."
                        + std::to_string(BOOST_VERSION / 100 % 1000) + "."
                        + std::to_string(BOOST_VERSION % 100);
    return env->NewStringUTF(version.c_str());
}

}
