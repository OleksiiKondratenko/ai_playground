package com.aiplayground.mathlib

class NativeMath {

    companion object {
        init {
            System.loadLibrary("mathlib")
        }
    }

    external fun nativeAdd(a: Int, b: Int): Int

    external fun nativeMultiply(a: Int, b: Int): Int

    external fun nativeBoostVersion(): String

    fun add(a: Int, b: Int): Int = nativeAdd(a, b)

    fun multiply(a: Int, b: Int): Int = nativeMultiply(a, b)

    fun boostVersion(): String = nativeBoostVersion()
}
