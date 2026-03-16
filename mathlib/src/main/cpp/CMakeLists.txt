cmake_minimum_required(VERSION 3.22.1)
project("mathlib")

if(CONAN_OUTPUT_BASE AND ANDROID_ABI)
    list(PREPEND CMAKE_PREFIX_PATH "${CONAN_OUTPUT_BASE}/${ANDROID_ABI}")
endif()

find_package(cairo REQUIRED)

add_library(mathlib SHARED
    native_math.cpp
)

target_link_libraries(mathlib
    android
    log
    cairo::cairo
)
