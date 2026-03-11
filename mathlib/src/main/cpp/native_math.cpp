#include <jni.h>

extern "C" {

JNIEXPORT jint JNICALL
Java_com_aiplayground_mathlib_NativeMath_nativeAdd(JNIEnv *, jobject, jint a, jint b) {
    return a + b;
}

JNIEXPORT jint JNICALL
Java_com_aiplayground_mathlib_NativeMath_nativeMultiply(JNIEnv *, jobject, jint a, jint b) {
    return a * b;
}

}
