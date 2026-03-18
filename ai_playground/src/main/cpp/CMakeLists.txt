cmake_minimum_required(VERSION 3.22.1)
project("mathlib")

# TODO:
# Not clear how to properly get build type in gradle for conan configuration
# hence: always release, with flags for debug if needed
set(CMAKE_BUILD_TYPE "Release")

if(CONAN_OUTPUT_BASE AND ANDROID_ABI)
    list(APPEND CMAKE_FIND_ROOT_PATH "${CONAN_OUTPUT_BASE}/${ANDROID_ABI}/build/${CMAKE_BUILD_TYPE}/generators")
endif()

find_package(nlohmann_json REQUIRED)
find_package(ZLIB REQUIRED CONFIG)

add_library(mathlib SHARED
    native_math.cpp
)

target_link_libraries(mathlib
    android
    log
    ZLIB::ZLIB
    nlohmann_json::nlohmann_json
)
