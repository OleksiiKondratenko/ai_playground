cmake_minimum_required(VERSION 3.22.1)
project("mathlib")

add_library(mathlib SHARED
    native_math.cpp
)

target_link_libraries(mathlib
    android
    log
)
