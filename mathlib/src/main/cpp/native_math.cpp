#include <jni.h>
#include <cairo.h>

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
Java_com_aiplayground_mathlib_NativeMath_nativeCairoVersion(JNIEnv *env, jobject) {
    const char *version = cairo_version_string();
    return env->NewStringUTF(version);
}

}
